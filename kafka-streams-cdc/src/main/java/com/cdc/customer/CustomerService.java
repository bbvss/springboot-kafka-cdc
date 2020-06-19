package com.cdc.customer;

import com.cdc.Topics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {
  private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
  private final CustomerRepository customerRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();
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

  @KafkaListener(
          id = "customerGroup", /*groupId = "orders",*/
          topics = Topics.SERVER_1_DBO_CUSTOMERS)
  public void listenCustomersCDC(ConsumerRecord<?, ?> cr) throws JsonProcessingException {
    final JsonNode after = objectMapper.readTree(cr.value().toString()).get("payload").get("after");
    logger.info(String.format("after = %s", after));
    final String uuid = after.get("uuid").asText();
    final String firstName = after.get("first_name").asText();
    final String lastName = after.get("last_name").asText();
    final String email = after.get("email").asText();
    onEvent(
            CustomerDto.builder()
                    .uuid(uuid)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .build());
  }

  public long count() {
    return customerRepository.count();
  }
}
