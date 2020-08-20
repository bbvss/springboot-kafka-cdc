package com.cdc.customer;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
  private final CustomerRepository customerRepository;
  private CustomerEventListener listener;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public void register(CustomerEventListener listener) {
    this.listener = listener;
  }

  public void onEvent(CustomerDto customerDto) {
    if (listener != null) {
      listener.onData(customerDto);
    }
  }

  public void onComplete() {
    if (listener != null) {
      listener.processComplete();
    }
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

  public Flux<Customer> countAll() {
    // Simulate big list of data, streaming it every 2 second delay
    return Flux.fromIterable(customerRepository.findAll()).delayElements(Duration.ofSeconds(2));
  }

  public void delete(Integer id) {
    customerRepository.deleteById(id);
  }

  public Customer insert(Customer customer) {
    return customerRepository.save(customer);
  }

  public Customer update(Customer customer) {
    return customerRepository.save(customer);
  }

  public Optional<Customer> findById(Integer id) {
    return customerRepository.findById(id);
  }

  // FLUX
  //      onEvent(
  //              CustomerDto.builder()
  //                      .uuid(uuid)
  //                      .firstName(firstName)
  //                      .lastName(lastName)
  //                      .email(email)
  //                      .build());
}
