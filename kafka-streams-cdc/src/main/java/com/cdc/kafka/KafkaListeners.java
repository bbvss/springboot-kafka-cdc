package com.cdc.kafka;

import com.cdc.Topics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class KafkaListeners {

  private static final Logger logger = LoggerFactory.getLogger(KafkaListeners.class);
  private final CountDownLatch latch = new CountDownLatch(3);
  private final ObjectMapper objectMapper = new ObjectMapper();

  //    @KafkaListener(id = "myTopicGroup", /*groupId = "myTopic",*/ topics = Topics.MY_TOPIC)
  //    public void listenMyTopic(ConsumerRecord<?, ?> cr) {
  //        logger.info("myTopic {}", cr);
  //        if (cr.offset() == 0) {
  //            logger.info("{} all messages consumed", Topics.MY_TOPIC);
  //        }
  //        latch.countDown();
  //    }

  @KafkaListener(id = "orderGroup", /*groupId = "orders",*/ topics = Topics.SERVER_1_DBO_ORDERS)
  public void listenOrdersCDC(ConsumerRecord<?, ?> cr) {
    logger.info("CDC Order {} {}", Topics.SERVER_1_DBO_ORDERS, cr);
    latch.countDown();
  }

  @KafkaListener(
          id = "customerGroup", /*groupId = "orders",*/
          topics = Topics.SERVER_1_DBO_CUSTOMERS)
  public void listenCustomersCDC(ConsumerRecord<?, ?> cr) throws JsonProcessingException {
    final JsonNode payload = objectMapper.readTree(cr.value().toString()).get("payload");
    if (payload != null) {
      logger.info(String.format("CDC Customer: payload = %s", payload));
    }
  }
}
