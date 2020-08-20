package com.cdc.customer;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class CustomerControllerFlux {

    private final CustomerService customerService;
    private final Flux<CustomerDto> bridge;

    public CustomerControllerFlux(CustomerService customerService) {
        this.customerService = customerService;
        this.bridge = createBridge().publish().autoConnect().cache(10).log();
    }

    @CrossOrigin(origins = {"http://localhost:8081", "http://localhost:8080"})
    @GetMapping(value = "/customersflux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CustomerDto> getCustomers() {
        return bridge;
        //    Random r = new Random();
        //    int low = 0;
        //    int high = 50;
        //    return Flux.fromStream(
        //            Stream.generate(() -> r.nextInt(high - low) + low)
        //                .map(s -> String.valueOf(s))
        //                .peek(
        //                    (msg) -> {
        //                      logger.info(msg);
        //                    }))
        //        .map(s -> Integer.valueOf(s))
        //        .delayElements(Duration.ofSeconds(1));
    }

    private Flux<CustomerDto> createBridge() {
        return Flux.create(
                sink -> { // (2)
                    customerService.register(
                            new CustomerEventListener() {
                                @Override
                                public void processComplete() {
                                    sink.complete();
                                }

                                @Override
                                public void onData(CustomerDto data) {
                                    sink.next(data);
                                }
                            });
                });
    }
}
