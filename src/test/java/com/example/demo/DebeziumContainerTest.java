package com.example.demo;

import com.jayway.jsonpath.JsonPath;
import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.debezium.testing.testcontainers.DebeziumContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DebeziumContainerTest {

  private static final Network network = Network.newNetwork();
  public static PostgreSQLContainer<?> postgresContainer =
          new PostgreSQLContainer<>("debezium/postgres")
                  .withNetwork(network)
                  .withNetworkAliases("postgres");
  private static final KafkaContainer kafkaContainer = new KafkaContainer("latest").withNetwork(network);
  public static DebeziumContainer debeziumContainer =
          new DebeziumContainer("1.1.2.Final")
                  .withNetwork(network)
                  .withKafka(kafkaContainer)
                  .dependsOn(kafkaContainer);

  @BeforeAll
  public static void startContainers() {
    Startables.deepStart(Stream.of(kafkaContainer, postgresContainer, debeziumContainer)).join();
  }

  @Test
  public void canRegisterPostgreSqlConnector() throws Exception {
    try (Connection connection = getConnection(postgresContainer);
         Statement statement = connection.createStatement();
         KafkaConsumer<String, String> consumer = getConsumer(kafkaContainer)) {

      statement.execute("create schema todo");
      statement.execute(
              "create table todo.Todo (id int8 not null, " + "title varchar(255), primary key (id))");
      statement.execute("alter table todo.Todo replica identity full");
      statement.execute("insert into todo.Todo values (1, " + "'Learn CDC')");
      statement.execute("insert into todo.Todo values (2, " + "'Learn Debezium')");

      ConnectorConfiguration connector =
              ConnectorConfiguration.forJdbcContainer(postgresContainer)
                      .with("database.server.name", "dbserver1");

      debeziumContainer.registerConnector("my-connector", connector);

      consumer.subscribe(Arrays.asList("dbserver1.todo.todo"));

      List<ConsumerRecord<String, String>> changeEvents = drain(consumer, 2);

      assertThat(JsonPath.<Integer>read(changeEvents.get(0).key(), "$.id")).isEqualTo(1);
      assertThat(JsonPath.<String>read(changeEvents.get(0).value(), "$.op")).isEqualTo("r");
      assertThat(JsonPath.<String>read(changeEvents.get(0).value(), "$.after.title"))
              .isEqualTo("Learn CDC");

      assertThat(JsonPath.<Integer>read(changeEvents.get(1).key(), "$.id")).isEqualTo(2);
      assertThat(JsonPath.<String>read(changeEvents.get(1).value(), "$.op")).isEqualTo("r");
      assertThat(JsonPath.<String>read(changeEvents.get(1).value(), "$.after.title"))
              .isEqualTo("Learn Debezium");

      consumer.unsubscribe();
    }
  }

  // Helper methods below

  private Connection getConnection(PostgreSQLContainer<?> postgresContainer) throws SQLException {

    return DriverManager.getConnection(
            postgresContainer.getJdbcUrl(),
            postgresContainer.getUsername(),
            postgresContainer.getPassword());
  }

  private KafkaConsumer<String, String> getConsumer(KafkaContainer kafkaContainer) {

    return new KafkaConsumer<>(
            ImmutableMap.of(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    kafkaContainer.getBootstrapServers(),
                    ConsumerConfig.GROUP_ID_CONFIG,
                    "tc-" + UUID.randomUUID(),
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                    "earliest"),
            new StringDeserializer(),
            new StringDeserializer());
  }

  private List<ConsumerRecord<String, String>> drain(
          KafkaConsumer<String, String> consumer, int expectedRecordCount) {

    List<ConsumerRecord<String, String>> allRecords = new ArrayList<>();

    Unreliables.retryUntilTrue(
            10,
            TimeUnit.SECONDS,
            () -> {
              consumer.poll(Duration.ofMillis(50)).iterator().forEachRemaining(allRecords::add);

              return allRecords.size() == expectedRecordCount;
            });

    return allRecords;
  }
}
