package com.jm.futelove.mappers;

import com.jm.futelove.dto.ImageDTO;
import com.jm.futelove.dto.UserDTO;
import com.jm.futelove.entity.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageDTO toDTO(Image entity){
        return ImageDTO.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .url(entity.getUrl())
                .userId(entity.getUser().getId())
                .build();
    }

    public Image toEntity(ImageDTO dto) {
        return Image.builder()
                .id(dto.getId())
                .fileName(dto.getFileName())
                .url(dto.getUrl())
                .user(null)
                .build();
    }
}
