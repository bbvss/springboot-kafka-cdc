package com.cdc.kafka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    private final KafkaService kafkaService;

    public KafkaController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping("/send-messages")
    public void sendMessages(@RequestParam int count) {
        try {
            kafkaService.send(count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
