package com.jm.futelove.services;

import com.jm.futelove.dto.WhatsAppMessageDTO;
import com.jm.futelove.dto.WhatsAppMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class WhatsAppService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    private final WebClient webClient;

    @Value("${whatsapp.api.url}")
    private String apiUrl;
    @Value("${whatsapp.api.token}")
    private String apiToken;
    @Value("${whatsapp.phone.number.id}")
    private String phoneNumberId;

    public WhatsAppService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<WhatsAppMessageResponse> sendMessage(WhatsAppMessageDTO dto) {
        return webClient.post()
                .uri(apiUrl, uriBuilder -> uriBuilder.build(phoneNumberId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(Map.of(
                        "messaging_product", "whatsapp",
                        "to", dto.getPhoneNumber(),
                        "type", "text",
                        "text", Map.of("body", dto.getMessage())
                ))
                .retrieve()
                .bodyToMono(WhatsAppMessageResponse.class);
    }

    public WhatsAppMessageResponse sendImageMessage(WhatsAppMessageDTO dto) {
        return webClient.post()
                .uri(apiUrl, uriBuilder -> uriBuilder.build(phoneNumberId))
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "messaging_product", "whatsapp",
                        "to", dto.getPhoneNumber(),
                        "type", "image",
                        "image", Map.of("link", dto.getImage().getFirst().getLink(), "caption",  dto.getImage().getFirst().getCaption())
                ))
                .retrieve()
                .bodyToMono(WhatsAppMessageResponse.class)
                .block();
    }
}

