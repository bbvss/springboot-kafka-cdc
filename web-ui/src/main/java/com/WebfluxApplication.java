package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class WebfluxApplication {

  private Logger logger = LoggerFactory.getLogger(WebfluxApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(WebfluxApplication.class, args);
  }

  @Bean
  WebClient getWebClient() {
    return WebClient.create("http://localhost:8080");
  }

  //  @Bean
  //  CommandLineRunner demo(WebClient client) {
  //    return args -> {
  //      client
  //          .get()
  //          .uri("/customers")
  //          .accept(MediaType.TEXT_EVENT_STREAM)
  //          .retrieve()
  //          .bodyToFlux(CustomerDto.class)
  //          .map(s -> String.valueOf(s))
  //          .subscribe(
  //              msg -> {
  //                logger.info(msg);
  //              });
  //    };
  //  }
}
