package com.jm.futelove.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRequest {

    @NotBlank
    private String message;
    private String model = "deepseek-chat";

}