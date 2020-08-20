package com.reactive;

import java.io.Serializable;

public class CustomerDto implements Serializable {
  public String uuid;
  private String firstName;
  private String lastName;
  private String email;

  public CustomerDto() {}

  public CustomerDto(String uuid, String firstName, String lastName, String email) {
    this.uuid = uuid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String toString() {
    return "Customer(uuid="
        + this.getUuid()
        + ", firstName="
        + this.getFirstName()
        + ", lastName="
        + this.getLastName()
        + ", email="
        + this.getEmail()
        + ")";
  }
}
