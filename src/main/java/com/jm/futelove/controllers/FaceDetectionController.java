package com.jm.futelove.controllers;

import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.Problem;
import com.jm.futelove.services.FaceDetectionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/facial")
@Data
@AllArgsConstructor
public class FaceDetectionController {

    private final FaceDetectionService faceDetectionService;

    @PostMapping("/detect")
    public ResponseEntity<?> detectFaces(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(faceDetectionService.detectFacesOpenCV(file));
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
