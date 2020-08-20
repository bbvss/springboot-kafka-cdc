package com.reactive.controller;

import com.reactive.CustomerDto;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CustomerPublisher {

  @Autowired private WebClient webClient;

  @Autowired private Subscriber<CustomerDto> subscriber;

  @Scheduled(fixedRate = 3000)
  public void publish() {
    this.webClient
        .get()
        .retrieve()
        .bodyToFlux(CustomerDto.class)
        .subscribe(this.subscriber::onNext);

  }
}
