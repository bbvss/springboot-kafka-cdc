package com.reactive.controller;

import com.reactive.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class CustomerController {

  @Autowired private Flux<CustomerDto> flux;

//  @CrossOrigin(origins = {"http://localhost:8081", "http://localhost:8080"})
  @GetMapping(value = "/customer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<CustomerDto> getJoke() {
    return flux;
  }
}
