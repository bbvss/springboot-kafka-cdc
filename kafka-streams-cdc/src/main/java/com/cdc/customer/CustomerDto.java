package com.cdc.customer;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerDto implements Serializable {

    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
}
