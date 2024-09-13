package com.jm.futelove.services;

import com.jm.futelove.controllers.GoogleStorageController;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.ProblemType;
import com.jm.futelove.execption.Response;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;

@Service
public class FaceDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleStorageController.class);

    @Value("classpath:opencv/haarcascades/haarcascade_frontalface_alt.xml")
    private Resource xmlResource;

    public Response detectFacesOpenCV(@RequestParam("file") MultipartFile file) {
        try {
            /* Carregar o classificador de rosto Haar Cascade */
            Path tempFile = Files.createTempFile("haarcascade_frontalface_alt", ".xml");
            try (InputStream xmlStream = xmlResource.getInputStream()) {
                Files.copy(xmlStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            CascadeClassifier faceDetector = new CascadeClassifier(tempFile.toString());

            /* Converter a imagem recebida em Mat */
            Mat image = imdecode(new Mat(file.getBytes()), Imgcodecs.IMREAD_COLOR);

            /* Detectar rostos */
            RectVector faceDetections = new RectVector();
            faceDetector.detectMultiScale(image, faceDetections);

            /* Retornar o número de rostos detectados */
            return Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("Faces detectable: " + faceDetections.size())
                    .build();

        } catch (Exception e) {
            logger.info("Error to process file: " + e.getMessage());
            ProblemType problemType = ProblemType.ERROR_FACE_DETECTED;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(),
                    "Error to process file: " + file.getOriginalFilename());
        }
    }
}