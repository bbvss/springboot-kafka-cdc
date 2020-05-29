package com.example.demo.kafka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

  private final KafkaService kafkaService;

  public KafkaController(KafkaService kafkaService) {
    this.kafkaService = kafkaService;
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    try {
      kafkaService.send();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return String.format("Hello my %s!", name);
  }
}
