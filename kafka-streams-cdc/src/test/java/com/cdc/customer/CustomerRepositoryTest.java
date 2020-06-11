package com.cdc.customer;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MSSQLServerContainer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@ContextConfiguration(initializers = {CustomerRepositoryTest.Initializer.class})
@AutoConfigureTestDatabase(replace = NONE)
public class CustomerRepositoryTest {

    @ClassRule
    public static MSSQLServerContainer mssqlserver =
            new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2019-CU4-ubuntu-16.04");

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testCustomerRepository() {
        Customer customer1 = new Customer("Luis", "Trenker", "luis.trenker@kafka.ch");
        Customer customer2 = new Customer("Sepp", "Forcher", "sepp.forcher@kafka.ch");
        // save user, verify has ID value after save
        assertNull(customer1.getId());
        assertNull(customer2.getId()); // null before save
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        assertNotNull(customer1.getId());
        assertNotNull(customer2.getId());
        /*Test data retrieval*/
        Customer customerA = customerRepository.findByEmail("luis.trenker@kafka.ch");
        assertNotNull(customerA);
        assertEquals("luis.trenker@kafka.ch", customerA.getEmail());
        /*Get all users, list should only have two*/
        assertEquals(2, customerRepository.findAll().size());
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            mssqlserver.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + mssqlserver.getJdbcUrl(),
                    "spring.datasource.username=" + mssqlserver.getUsername(),
                    "spring.datasource.password=" + mssqlserver.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
