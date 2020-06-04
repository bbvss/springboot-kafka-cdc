package com.example.demo.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user_tbl")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;
  private int age;

  public User(String name, int age) {
    this.name = name;
    this.age = age;
  }
}
