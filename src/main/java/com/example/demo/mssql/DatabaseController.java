package com.example.demo.mssql;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

  private final DatabaseService databaseService;

  public DatabaseController(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @GetMapping("/insert")
  public String insert() {
    databaseService.insert();
    return String.format("Count users %s", databaseService.count());
  }
}
