package com.jm.futelove.dto;

import lombok.Data;

@Data
public class ChatResponse {
    private String answer;
    private boolean success;
    private String error;

    public ChatResponse() {}

    public ChatResponse(String answer, boolean success) {
        this.answer = answer;
        this.success = success;
    }

    public ChatResponse(String error) {
        this.success = false;
        this.error = error;
    }

    public static ChatResponse success(String answer) {
        return new ChatResponse(answer, true);
    }

    public static ChatResponse error(String error) {
        return new ChatResponse(error);
    }
}