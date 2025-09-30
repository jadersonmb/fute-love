package com.jm.futelove.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppMessageResponse {

    @JsonProperty("messaging_product")
    private String messagingProduct;
    @JsonProperty("contacts")
    private List<Contacts> contacts;
    private List<Messages> Messages;


    @Data
    public static class Contacts {
        private String input;
        @JsonProperty("wa_id")
        private String waid;
    }

    @Data
    public static class Messages {
        private String id;
    }
}
