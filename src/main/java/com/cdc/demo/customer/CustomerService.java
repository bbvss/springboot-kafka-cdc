package com.cdc.demo.customer;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public void insert(int count) {
    for (int i = 0; i < count; i++) {
      Customer customer =
              new Customer(
                      "firstName " + i, "lastName " + i, "firstName" + i + ".lastName" + i + "@kafka.ch");
      customerRepository.save(customer);
    }
  }

  public long count() {
    return customerRepository.count();
  }
}
