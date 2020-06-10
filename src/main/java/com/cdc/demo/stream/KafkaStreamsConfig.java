package com.cdc.demo.stream;

import com.cdc.demo.Topics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Properties;

@Configuration
@DependsOn("topicCustomer")
public class KafkaStreamsConfig {

  @Bean
  @DependsOn("topicCustomer")
  public KafkaStreams kafkaStreams(@Value("${spring.application.name}") String appName) {
    final Properties props = new Properties();
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
    props.put(StreamsConfig.CLIENT_ID_CONFIG, appName + " Client");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    final KafkaStreams streams = new KafkaStreams(kafkaStreamTopology(), props);
    cleanup(streams);
    streams.start();
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    return streams;
  }

  private void cleanup(KafkaStreams streams) {
    // Always (and unconditionally) clean local state prior to starting the processing topology.
    // We opt for this unconditional call here because this will make it easier for you to play
    // around with the example
    // when resetting the application for doing a re-run (via the Application Reset Tool,
    // http://docs.confluent.io/current/streams/developer-guide.html#application-reset-tool).
    //
    // The drawback of cleaning up local state prior is that your app must rebuilt its local state
    // from scratch, which
    // will take time and will require reading all the state-relevant data from the Kafka cluster
    // over the network.
    // Thus in a production scenario you typically do not want to clean up always as we do here but
    // rather only when it
    // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line
    // flag for your app).
    // See `ApplicationResetExample.java` for a production-like example.
    streams.cleanUp();
  }

  @Bean
  @DependsOn("topicCustomer")
  public Topology kafkaStreamTopology() {
    final StreamsBuilder builder = new StreamsBuilder();

    KStream<String, String> textLines = builder.stream(Topics.SERVER_1_DBO_CUSTOMERS);
    textLines.foreach(
            (key, value) -> System.out.println("STREAM: key = " + key + " value = " + value));
    //    Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

    //        final Topology topology = builder.build();
    //        final CountDownLatch latch = new CountDownLatch(1);
    //        try (KafkaStreams streams = kafkaStreams()) {
    //            streams.start();
    //            latch.await();
    //            //      Thread.sleep(30000);
    //        }
    final Topology topology = builder.build();
    System.out.println("topology = " + topology.describe());
    return topology;
  }
}
