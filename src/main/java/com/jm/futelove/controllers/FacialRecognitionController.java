package com.jm.futelove.controllers;

import com.jm.futelove.execption.Response;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/facial/recognition/")
public class FacialRecognitionController {

    /* Armazenar as imagens e seus respectivos labels (nomes ou IDs)*/
    private List<Mat> images = new ArrayList<>();
    private List<Integer> labels = new ArrayList<>();
    private Map<Integer, String> labelToNameMap = new HashMap<>();

    private LBPHFaceRecognizer recognizer;

    public FacialRecognitionController() {
        /* Inicializa o reconhecedor LBPH*/
        recognizer = LBPHFaceRecognizer.create();
    }

    /* Endpoint para fazer upload de imagens e associá-las a um rótulo*/
    @PostMapping("/train")
    public ResponseEntity<?> uploadImageForTraining(@RequestParam("file") MultipartFile file, @RequestParam("label") String label) {
        try {
            /* Converte a imagem para grayscale e adiciona à lista de imagens*/
            Mat img = opencv_imgcodecs.imdecode(new Mat(file.getBytes()), opencv_imgcodecs.IMREAD_GRAYSCALE);
            images.add(img);

            /* Define um ID único para a pessoa e armazena o rótulo*/
            int personId = labels.size(); /* Pode usar outro critério para IDs únicos*/
            labels.add(personId);
            labelToNameMap.put(personId, label);

            return ResponseEntity.ok(Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("Imagem recebida e associada ao label: " + label)
                    .build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a imagem.");
        }
    }

    /* Endpoint para treinar o modelo com as imagens recebidas*/
    @PostMapping("/train/model")
    public ResponseEntity<?> trainModel() {
        if (images.isEmpty()) {
            return ResponseEntity.ok(Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Nenhuma imagem para treinar.")
                    .build());

        }

        /* Converter a lista de imagens para MatVector*/
        MatVector imagesMatVector = new MatVector(images.size());
        for (int i = 0; i < images.size(); i++) {
            imagesMatVector.put(i, images.get(i));
        }

        /* Converter a lista de labels para um Mat do tipo CV_32SC1 (inteiros de 32 bits)*/
        Mat labelsMat = new Mat(labels.size(), 1, opencv_core.CV_32SC1);
        for (int i = 0; i < labels.size(); i++) {
            labelsMat.ptr(i).putInt(labels.get(i));
        }

        /* Treinar o reconhecedor LBPH*/
        recognizer.train(imagesMatVector, labelsMat);

        return ResponseEntity.ok(Response.builder()
                .status(HttpStatus.OK.value())
                .message("Modelo treinado com sucesso!")
                .build());
    }

    /* Endpoint para salvar o modelo treinado em um arquivo*/
    @PostMapping("/train/saveModel")
    public ResponseEntity<?> saveModel() {
        if (recognizer != null) {
            recognizer.save("trained_model.xml");
            return ResponseEntity.ok(Response.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Modelo salvo em 'trained_model.xml'.")
                    .build());
        }
        return ResponseEntity.ok(Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Nenhum modelo treinado disponível.")
                .build());
    }

    /* Endpoint para reconhecimento facial*/
    @PostMapping("/recognize/image")
    public ResponseEntity<?> recognizeImage(@RequestParam("file") MultipartFile file) {
        try {
            /* Carregar a imagem enviada para reconhecimento*/
            Mat img = opencv_imgcodecs.imdecode(new Mat(file.getBytes()), opencv_imgcodecs.IMREAD_GRAYSCALE);

            /* Realizar reconhecimento*/
            int[] predictedLabel = new int[1];
            double[] confidence = new double[1];
            recognizer.predict(img, predictedLabel, confidence);

            /* Verificar resultado*/
            if (predictedLabel[0] >= 0 && labelToNameMap.containsKey(predictedLabel[0])) {
                String name = labelToNameMap.get(predictedLabel[0]);
                return ResponseEntity.ok("Reconhecido: " + name + " (Confiança: " + confidence[0] + ")");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa não reconhecida.");
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a imagem.");
        }
    }
}
