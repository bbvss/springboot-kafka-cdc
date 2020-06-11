package com.cdc.customer;

import org.reactivestreams.Subscriber;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CustomerPublisher {

    private final WebClient webClient;

    private Subscriber<CustomerDto> subscriber;

    public CustomerPublisher(WebClient webClient, Subscriber<CustomerDto> subscriber) {
        this.webClient = webClient;
        this.subscriber = subscriber;
    }

    @Scheduled(fixedRate = 3000)
    public void publish() {
        this.webClient
                .get()
                .retrieve()
                .bodyToMono(CustomerDto.class)
                .subscribe(this.subscriber::onNext);
    }
}
