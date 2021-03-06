package com;

import com.reactive.CustomerDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.DirectProcessor;

@Configuration
public class AppConfiguration {

//  private static final String JOKE_API_ENDPOINT = "http://localhost:8080/customersflux";

//    @Bean
//    public WebClient webClient(){
//        return WebClient.builder()
//                .baseUrl(JOKE_API_ENDPOINT)
//                .build();
//    }

    @Bean
    public DirectProcessor<CustomerDto> processor() {
        return DirectProcessor.create();
    }

}
