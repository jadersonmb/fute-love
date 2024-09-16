package com.jm.futelove.services;

import com.jm.futelove.dto.ImageDTO;
import com.jm.futelove.entity.Image;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.mappers.ImageMapper;
import com.jm.futelove.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {

    private final ImageRepository repository;
    private final ImageMapper mapper;
    private final UserService userService;

    public ImageDTO save(ImageDTO imageDTO) {
        Image entity = mapper.toEntity(imageDTO);
        entity.setUser(userService.findByEntityId(imageDTO.getUserId()));

        return mapper.toDTO(repository.save(entity));
    }

    public ImageDTO findById(UUID id) {
        Image entity = repository.findById(id).orElseThrow(() -> new FuteLoveException("Not found image with id: " + id));
        entity.setUser(userService.findByEntityId(entity.getId()));

        return mapper.toDTO(entity);
    }
}
