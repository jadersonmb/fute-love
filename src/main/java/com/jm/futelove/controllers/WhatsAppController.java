package com.jm.futelove.controllers;

import com.jm.futelove.dto.WhatsAppMessageDTO;
import com.jm.futelove.dto.WhatsAppMessageResponse;
import com.jm.futelove.services.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/api/v1/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppController.class);

    private final WhatsAppService whatsAppService;

    @PostMapping("/send")
    public Mono<ResponseEntity<WhatsAppMessageResponse>> sendMessage(@RequestBody WhatsAppMessageDTO dto) {
        return whatsAppService.sendMessage(dto).map(ResponseEntity::ok);
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running!");
    }


    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveMessage(@RequestBody Map<String, Object> payload) {
        logger.info("Webhook recebido: {}", payload);

        var entry = ((List<Map<String, Object>>) payload.get("entry")).get(0);
        var changes = ((List<Map<String, Object>>) entry.get("changes")).get(0);
        var value = (Map<String, Object>) changes.get("value");
        var messages = (List<Map<String, Object>>) value.get("messages");

        if (messages != null) {
            var message = messages.getFirst();
            String from = (String) message.get("from");
            String type = (String) message.get("type");

            if ("text".equals(type)) {
                Map<String, Object> text = (Map<String, Object>) message.get("text");
                String body = (String) text.get("body");
                logger.info("Mensagem de texto recebida de {}: {}", from, body);
            } else if ("image".equals(type)) {
                Map<String, Object> image = (Map<String, Object>) message.get("image");
                String mediaId = (String) image.get("id");
                logger.info("Imagem recebida de {} com mediaId: {}", from, mediaId);
            }
        }
        return ResponseEntity.ok().build();
    }
}


