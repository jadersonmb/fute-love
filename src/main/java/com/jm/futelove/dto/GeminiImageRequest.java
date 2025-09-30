package com.jm.futelove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiImageRequest {
    private List<Content> contents;

    public static GeminiImageRequest forImageAndText(String prompt, String base64Image, String mimeType) {
        Part textPart = new Part(prompt, null);
        ImageData imageData = new ImageData(mimeType, base64Image);
        Part imagePart = new Part(null, imageData);
        return new GeminiImageRequest(List.of(new Content(List.of(textPart, imagePart))));
    }
}

