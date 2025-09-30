package com.jm.futelove.mappers;

import com.jm.futelove.dto.UserDTO;
import com.jm.futelove.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(Users entity){
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .lastName(entity.getLastName())
                .city(entity.getCity())
                .country(entity.getCountry())
                .documentNumber(entity.getDocumentNumber())
                .phoneNumber(entity.getPhoneNumber())
                .postalCode(entity.getPostalCode())
                .state(entity.getState())
                .street(entity.getStreet())
                .build();
    }

    public Users toEntity(UserDTO userDTO){
        return Users.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .lastName(userDTO.getLastName())
                .city(userDTO.getCity())
                .country(userDTO.getCountry())
                .documentNumber(userDTO.getDocumentNumber())
                .phoneNumber(userDTO.getPhoneNumber())
                .postalCode(userDTO.getPostalCode())
                .state(userDTO.getState())
                .street(userDTO.getStreet())
                .password(userDTO.getPassword())
                .build();
    }

    public Users toUpdate(Users entity) {
        entity.setName(entity.getName());
        entity.setHashCode(entity.getHashCode());
        entity.setLastName(entity.getLastName());
        entity.setCity(entity.getCity());
        entity.setCountry(entity.getCountry());
        entity.setDocumentNumber(entity.getDocumentNumber());
        entity.setPhoneNumber(entity.getPhoneNumber());
        entity.setPostalCode(entity.getPostalCode());
        entity.setState(entity.getState());
        entity.setStreet(entity.getStreet());
        return entity;
    }

}

