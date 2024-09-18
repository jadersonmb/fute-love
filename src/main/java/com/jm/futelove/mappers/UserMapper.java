package com.jm.futelove.mappers;

import com.jm.futelove.dto.UserDTO;
import com.jm.futelove.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User entity){
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public User toEntity(UserDTO userDTO){
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .build();
    }

    public User toUpdate(User entity) {
        entity.setName(entity.getName());
        entity.setHashCode(entity.getHashCode());
        return entity;
    }

}

