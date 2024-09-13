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
}
