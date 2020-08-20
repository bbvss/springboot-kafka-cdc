package com.cdc.customer;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping("/customers/insert")
  public String insert(@RequestParam int count) {
    customerService.insert(count);
    return String.format("Count users %s", customerService.count());
  }

  @PostMapping("/customers")
  public CustomerDto insert(@RequestBody CustomerDto dto) {
    return mapTo(customerService.insert(mapFrom(dto)));
  }

  @PutMapping("/customers/{id}")
  public CustomerDto put(@RequestBody CustomerDto dto, @PathVariable Integer id) {
    return mapTo(
            customerService
                    .findById(id)
                    .map(customer -> customerService.update(map(dto, customer)))
                    .orElseGet(() -> customerService.insert(mapFrom(dto))));
  }

  private Customer map(CustomerDto dto, Customer customer) {
    customer.setFirstName(dto.getFirstName());
    customer.setLastName(dto.getLastName());
    customer.setEmail(dto.getEmail());
    return customer;
  }

  @DeleteMapping("/customers/{id}")
  public void delete(@PathVariable Integer id) {
    customerService.delete(id);
  }

  private CustomerDto mapTo(Customer insert) {
    return new CustomerDto(
            insert.getUuid().toString(),
            insert.getFirstName(),
            insert.getLastName(),
            insert.getEmail());
  }

  private Customer mapFrom(CustomerDto dto) {
    return new Customer(UUID.randomUUID(), dto.getFirstName(), dto.getLastName(), dto.getEmail());
  }
}
