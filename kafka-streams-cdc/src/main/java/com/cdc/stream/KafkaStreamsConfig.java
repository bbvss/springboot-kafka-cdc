package com.cdc.stream;

import com.cdc.Topics;
import com.cdc.customer.CustomerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {
  private static final Logger logger = LoggerFactory.getLogger(KafkaStreamsConfig.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Bean
  public ConsumerFactory<String, CustomerDto> consumerFactory() {
    return new DefaultKafkaConsumerFactory(
            consumerConfigs(), new StringDeserializer(), new StringDeserializer());
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "customerGroup");
    return props;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CustomerDto>
  kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CustomerDto> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  @Bean
  public KafkaStreams kafkaStreams(@Value("${spring.application.name}") String appName) {
    final Properties props = new Properties();
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
    props.put(StreamsConfig.CLIENT_ID_CONFIG, appName + " Client");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    final KafkaStreams streams = new KafkaStreams(kafkaStreamTopology(), props);
    //    cleanup(streams);
    streams.start();
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    return streams;
  }

  private void cleanup(KafkaStreams streams) {
    //      // Always (and unconditionally) clean local state prior to starting the processing
    // topology.
    //      // We opt for this unconditional call here because this will make it easier for you to
    // play
    //      // around with the example
    //      // when resetting the application for doing a re-run (via the Application Reset Tool,
    //      //
    // http://docs.confluent.io/current/streams/developer-guide.html#application-reset-tool).
    //      //
    //      // The drawback of cleaning up local state prior is that your app must rebuilt its local
    //   state
    //      // from scratch, which
    //      // will take time and will require reading all the state-relevant data from the Kafka
    //   cluster
    //      // over the network.
    //      // Thus in a production scenario you typically do not want to clean up always as we do
    // here
    //   but
    //      // rather only when it
    //      // is truly needed, i.e., only under certain conditions (e.g., the presence of a command
    //   line
    //      // flag for your app).
    //      // See `ApplicationResetExample.java` for a production-like example.
    streams.cleanUp();
  }

  @Bean
  public Topology kafkaStreamTopology() {
    final StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> textLines = builder.stream(Topics.SERVER_1_DBO_CUSTOMERS);
    //      final JsonNode payload = objectMapper.readTree(cr.value().toString()).get("payload");
    //      if (payload != null) {
    //        logger.info(String.format("CDC Customer: payload = %s", payload));
    //      }
    textLines.foreach(
            (key, value) -> {
              try {
                if (value != null) {
                  final JsonNode payload = objectMapper.readTree(value).get("payload");
                  logger.info(String.format("STREAM Customers from CDC: payload = %s%n", payload));
                }
              } catch (JsonProcessingException e) {
                e.printStackTrace();
              }
            });
    return builder.build();
  }
}
