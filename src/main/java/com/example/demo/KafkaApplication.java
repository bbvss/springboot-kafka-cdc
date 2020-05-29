package com.example.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
@EnableKafka
public class KafkaApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(KafkaApplication.class);
  private final CountDownLatch latch = new CountDownLatch(3);
  @Autowired
  private KafkaTemplate<String, String> template;

  public static void main(String[] args) {
    SpringApplication.run(KafkaApplication.class, args);
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello my %s!", name);
  }

  @Override
  public void run(String... args) throws Exception {
    int y = 1;
    for (int i = 0; i < 1000000; i++) {
      this.template.send("myTopic", "key" + y, "foo" + y);
    }
    latch.await(60, TimeUnit.SECONDS);
    logger.info("All received");
  }

  @KafkaListener(
          topicPartitions = {
                  @TopicPartition(
                          topic = "myTopic",
                          partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
          })
  public void listen(ConsumerRecord<?, ?> cr) throws Exception {
    logger.info(cr.toString());
    latch.countDown();
  }
}
