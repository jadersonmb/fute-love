package com.jm.futelove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppMessageDTO {

    private String phoneNumber;
    private String message;
    private List<Image> image;

    @Data
    public static class Image {
        private String link;
        private String caption;
        private String mediaId;
    }
}

