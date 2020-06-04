package com.example.demo;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
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
@ContextConfiguration(initializers = {UserRepositoryTest.Initializer.class})
@AutoConfigureTestDatabase(replace = NONE)
public class UserRepositoryTest {

  @ClassRule
  public static MSSQLServerContainer mssqlserver =
          new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2019-CU4-ubuntu-16.04");

  @Autowired
  private UserRepository userRepository;

  @Test
  public void testFetchData() {
    User user1 = new User("Alice", 23);
    User user2 = new User("Bob", 38);
    // save user, verify has ID value after save
    assertNull(user1.getId());
    assertNull(user2.getId()); // null before save
    userRepository.save(user1);
    userRepository.save(user2);
    assertNotNull(user1.getId());
    assertNotNull(user2.getId());
    /*Test data retrieval*/
    User userA = userRepository.findByName("Bob");
    assertNotNull(userA);
    assertEquals(38, userA.getAge());
    /*Get all users, list should only have two*/
    final Iterable<User> users = userRepository.findAll();
    int count = 0;
    for (User user : users) {
      count++;
    }
    assertEquals(count, 2);
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
