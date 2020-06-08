package com.example.demo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaService {

  private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);
  private final CountDownLatch latch = new CountDownLatch(3);
  private final KafkaTemplate<String, String> template;

  public KafkaService(KafkaTemplate<String, String> template) {
    this.template = template;
  }

  public void send() throws InterruptedException {
    int y = 1;
    for (int i = 0; i < 1000000; i++) {
      template.send("myTopic", "key1" + y, "foo1" + y);
      y++;
    }
    logger.info("All sent");
    latch.await(10, TimeUnit.SECONDS);
  }
}
