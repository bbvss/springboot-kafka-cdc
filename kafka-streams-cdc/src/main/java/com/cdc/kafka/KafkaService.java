package com.cdc.kafka;

import com.cdc.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
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

    public void send(int count) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            final Message message = Message.builder().email("test" + i + "@test.ch").uuid(UUID.randomUUID().toString()).build();
            template.send(Topics.MY_TOPIC, "key" + i, message.toString());
        }
        logger.info("All sent");
        latch.await(10, TimeUnit.SECONDS);
    }
}
