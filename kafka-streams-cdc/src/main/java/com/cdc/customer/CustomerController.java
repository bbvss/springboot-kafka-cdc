package com.cdc.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class CustomerController {

    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    private final Flux<CustomerDto> bridge;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
        this.bridge = createBridge().publish().autoConnect().cache(10).log();
    }

    @RequestMapping("/")
    public String index(final Model model) {
        // loads 1 and display 1, stream data, data driven mode.
        model.addAttribute("movies", bridge);
        // classic, wait repository loaded all and display it.
        //model.addAttribute("movies", movieRepository.findAll());
        return "index";
    }

    @GetMapping("/insert")
    public String insert(@RequestParam int count) {
        customerService.insert(count);
        return String.format("Count users %s", customerService.count());
    }

    @CrossOrigin(origins = {"http://localhost:8081", "http://localhost:8080"})
    @GetMapping(value = "/customers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
