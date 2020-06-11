package com.cdc.customer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.DirectProcessor;

@Configuration
public class CustomerConfig {
    private static final String CUSTOMERS_API_ENDPOINT = "http://localhost:8080/customers";

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(CUSTOMERS_API_ENDPOINT).build();
    }

    @Bean
    public DirectProcessor<CustomerDto> processor() {
        return DirectProcessor.create();
    }
}
