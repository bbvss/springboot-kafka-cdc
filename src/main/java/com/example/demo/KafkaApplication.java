package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KafkaApplication {

  private static final Logger logger = LoggerFactory.getLogger(KafkaApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(KafkaApplication.class, args);
  }
}
