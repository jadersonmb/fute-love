package com.jm.futelove.controllers;


import com.jm.futelove.dto.ChatRequest;
import com.jm.futelove.dto.ChatResponse;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.Problem;
import com.jm.futelove.services.DeepSeekService;
import com.jm.futelove.services.GeminiService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Data
@AllArgsConstructor
public class IAController {

    private final DeepSeekService deepSeekService;
    private final GeminiService geminiService;

    @PostMapping("/deepseek/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest chatRequest) {
        ChatResponse response = deepSeekService.sendMessage(chatRequest.getMessage());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/gemini/chat")
    public Mono<ResponseEntity<String>> chatGemini(@Valid @RequestBody ChatRequest chatRequest) {
        return geminiService.generateTextFromPrompt(chatRequest.getMessage())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar texto: " + e.getMessage())));
    }

    @PostMapping("/gemini/image")
    public Mono<ResponseEntity<String>> processImageGemini(@RequestParam("prompt") String prompt,
                                                     @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Por favor, selecione um arquivo para upload."));
        }

        try {
            byte[] imageBytes = file.getBytes();
            String mimeType = file.getContentType();
            if (mimeType == null || (!mimeType.startsWith("image/jpeg") && !mimeType.startsWith("image/png") && !mimeType.startsWith("image/gif"))) {
                return Mono.just(ResponseEntity.badRequest().body("Formato de imagem não suportado. Use JPEG, PNG ou GIF."));
            }

            return geminiService.generateTextFromImage(prompt, imageBytes, mimeType)
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar imagem: " + e.getMessage())));

        } catch (IOException e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao ler o arquivo: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running!");
    }

    @ExceptionHandler({FuteLoveException.class})
    public ResponseEntity<Object> FuteLoveException(FuteLoveException ex) {
        Problem problem = createProblemBuild(ex.getStatus(), ex.getDetails(), ex.getType(), ex.getTitle())
                .build();
        return ResponseEntity.badRequest().body(problem);
    }

    private Problem.ProblemBuilder createProblemBuild(Integer status, String detail, String type, String title) {
        return Problem.builder()
                .status(status)
                .details(detail)
                .type(type)
                .title(title);
    }
}
