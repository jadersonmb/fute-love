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
                .build();
    }

    public Users toEntity(UserDTO userDTO){
        return Users.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .build();
    }

    public Users toUpdate(Users entity) {
        entity.setName(entity.getName());
        entity.setHashCode(entity.getHashCode());
        return entity;
    }

}

