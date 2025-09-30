package com.jm.futelove.services;


import com.jm.futelove.dto.GeminiImageRequest;
import com.jm.futelove.dto.GeminiRequest;
import com.jm.futelove.dto.GeminiResponse;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.ProblemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final MessageSource messageSource;

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String textApiUrl;

    public GeminiService(WebClient.Builder webClientBuilder, MessageSource messageSource) {
        this.webClient = webClientBuilder.build();
        this.messageSource = messageSource;
    }

    public Mono<String> generateTextFromPrompt(String prompt) {
        GeminiRequest request = GeminiRequest.forText(prompt);
        ProblemType problemType = ProblemType.ERROR_GEMINI;
        String messageDetails = messageSource.getMessage(problemType.getMessageSource(), new Object[]{""}, LocaleContextHolder.getLocale());


        return webClient.post()
                .uri(textApiUrl, uriBuilder -> uriBuilder.build(apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> {
                    if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                        return Objects.requireNonNull(response.getCandidates().getFirst().getContent().getParts().getFirst().getText());
                    }
                    throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getTitle(), problemType.getUri(), messageDetails);
                });
    }

    public Mono<String> generateTextFromImage(String prompt, byte[] imageBytes, String mimeType) {
        /* Converter a imagem para Base64 */
        String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
        GeminiImageRequest request = GeminiImageRequest.forImageAndText(prompt, base64Image, mimeType);
        ProblemType problemType = ProblemType.ERROR_GEMINI;
        String messageDetails = messageSource.getMessage(problemType.getMessageSource(), new Object[]{""}, LocaleContextHolder.getLocale());

        return webClient.post()
                .uri(textApiUrl, uriBuilder -> uriBuilder.build(apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> {
                    if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                        return Objects.requireNonNull(response.getCandidates().getFirst().getContent().getParts().getFirst().getText());
                    }
                    throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getTitle(), problemType.getUri(), messageDetails);
                });
    }
}
