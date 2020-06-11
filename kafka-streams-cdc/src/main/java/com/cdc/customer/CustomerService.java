package com.cdc.customer;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void insert(int count) {
        for (int i = 0; i < count; i++) {
            final Customer customer =
                    new Customer(UUID.randomUUID(), "firstName ", "lastName ", "firstName.lastName@kafka.ch");
            customerRepository.save(customer);
        }
    }

    public long count() {
        return customerRepository.count();
    }
}
