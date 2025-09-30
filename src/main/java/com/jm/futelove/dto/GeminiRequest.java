package com.jm.futelove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiRequest {
    private List<Content> contents;

    public static GeminiRequest forText(String prompt) {
        return new GeminiRequest(List.of(new Content(List.of(new Part(prompt, null)))));
    }
}
