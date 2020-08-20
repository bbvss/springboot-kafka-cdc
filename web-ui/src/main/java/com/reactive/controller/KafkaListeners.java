//package com.reactive.controller;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.CountDownLatch;
//
//@Service
//public class KafkaListeners {
//
//  private static final Logger logger = LoggerFactory.getLogger(KafkaListeners.class);
//  private final CountDownLatch latch = new CountDownLatch(3);
//
//  @KafkaListener(
//      id = "customerGroup", /*groupId = "myTopic",*/
//      topics = Topics.SERVER_1_DBO_CUSTOMERS)
//  public void listenMyTopic(ConsumerRecord<?, ?> cr) {
//    logger.info("myTopic {}", cr);
//    if (cr.offset() == 0) {
//      logger.info("{} all messages consumed", Topics.MY_TOPIC);
//    }
//    latch.countDown();
//  }
//}
