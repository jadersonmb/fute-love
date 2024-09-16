package com.jm.futelove.controllers;

import com.jm.futelove.dto.ImageDTO;
import com.jm.futelove.services.GoogleStorageService;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.Problem;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/storage")
@Data
@AllArgsConstructor
public class GoogleStorageController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleStorageController.class);

    private final GoogleStorageService googleStorageService;

    /***
     * Método para fazer o upload de um arquivo para o Google Cloud Storage
     * @param file
     * @param userId
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(googleStorageService.uploadFile(file, userId));
    }

    /***
     *  Método para buscar o arquivo de um perfil
     * @param profileId
     * @param fileName
     * @return
     */
    @GetMapping("/download/{profileId}/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable UUID profileId, @PathVariable String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(googleStorageService.downloadFile(profileId, fileName));
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