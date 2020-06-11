package com.cdc.customer;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class CustomerController {

    private final CustomerService customerService;
    private final Flux<CustomerDto> flux;

    public CustomerController(CustomerService customerService, Flux<CustomerDto> flux) {
        this.customerService = customerService;
        this.flux = flux;
    }

    @GetMapping("/insert")
    public String insert(@RequestParam int count) {
        customerService.insert(count);
        return String.format("Count users %s", customerService.count());
    }

    @GetMapping(value = "/customers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CustomerDto> getCustomers() {
        return flux;
    }
}
