package com.cdc.demo.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping("/insert")
  public String insert(@RequestParam int count) {
    customerService.insert(count);
    return String.format("Count users %s", customerService.count());
  }
}
