package com.example.demo.mssql;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

  private final UserRepository userRepository;

  public DatabaseService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void insert() {
    for (int i = 0; i < 1000; i++) {
      User user = new User("name " + i, 33 + i);
      userRepository.save(user);
    }
  }

  public long count() {
    return userRepository.count();
  }
}
