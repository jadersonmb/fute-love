package com.jm.futelove.dto;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "user")
public class UserDTO {

    private UUID id;
    private String name;
    private String email;
    private String lastName;
    private String documentNumber;
    private String phoneNumber;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String password;
}
