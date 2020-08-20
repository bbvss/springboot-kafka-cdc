package com;

import com.reactive.CustomerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@EnableScheduling
@SpringBootApplication
public class WebfluxApplication {

  private final Logger logger = LoggerFactory.getLogger(WebfluxApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(WebfluxApplication.class, args);
  }

  @Bean
  WebClient getWebClient() {
    return WebClient.create("http://localhost:8080");
  }

  // Either run via commandline to see output in console
  // or run http://localhost:8081/index.html to see in browser
  @Bean
  CommandLineRunner demo(WebClient client) {
    return args ->
            client
                    .get()
                    .uri("/customersflux")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(CustomerDto.class)
                    .map(String::valueOf)
                    .subscribe(msg -> logger.info(msg));
  }
}
