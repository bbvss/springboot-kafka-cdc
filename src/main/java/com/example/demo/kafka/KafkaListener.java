package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class KafkaListener {

  private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);
  private final CountDownLatch latch = new CountDownLatch(3);

  @org.springframework.kafka.annotation.KafkaListener(
          topicPartitions = {
                  @TopicPartition(
                          topic = "myTopic",
                          partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
          })
  public void listen(ConsumerRecord<?, ?> cr) {
    logger.info(cr.toString());
    latch.countDown();
  }
}
