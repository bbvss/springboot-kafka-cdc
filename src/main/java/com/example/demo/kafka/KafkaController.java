package com.example.demo.kafka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

  private final KafkaService kafkaService;

  public KafkaController(KafkaService kafkaService) {
    this.kafkaService = kafkaService;
  }

  @GetMapping("/send-messages")
  public void sendMessages() {
    try {
      kafkaService.send();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
