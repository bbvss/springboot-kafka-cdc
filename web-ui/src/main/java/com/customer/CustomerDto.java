package com.customer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CustomerDto implements Serializable {
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
}
