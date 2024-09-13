package com.jm.futelove.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {

    private UUID id;
    private String fileName;
    private String url;
    private UUID userId;
}
