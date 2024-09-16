package com.jm.futelove.services;

import com.jm.futelove.controllers.GoogleStorageController;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.ProblemType;
import com.jm.futelove.execption.Response;
import lombok.SneakyThrows;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
public class FaceDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleStorageController.class);

    @Value("classpath:opencv/haarcascades/haarcascade_frontalface_alt.xml")
    private Resource xmlResource;
    private LBPHFaceRecognizer faceRecognizer;
    private final UserService userService;

    public FaceDetectionService(UserService userService) {
        this.faceRecognizer = LBPHFaceRecognizer.create();
        this.userService = userService;
    }

    public Response detectFacesOpenCV(@RequestParam("file") MultipartFile file) {
        try {
            Path tempFile = Files.createTempFile("haarcascade_frontalface_alt", ".xml");
            try (InputStream xmlStream = xmlResource.getInputStream()) {
                Files.copy(xmlStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            CascadeClassifier faceDetector = new CascadeClassifier(tempFile.toString());

            /* Converte a imagem para grayscale e adiciona à lista de imagens*/
            Mat image = opencv_imgcodecs.imdecode(new Mat(file.getBytes()), opencv_imgcodecs.IMREAD_GRAYSCALE);

            /* Detectar rostos */
            RectVector faceDetections = new RectVector();
            faceDetector.detectMultiScale(image, faceDetections);

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

    public Response processVideo(String videoFilePath) {

        try {
            /* Abrir o vídeo */
            VideoCapture videoCapture = new VideoCapture(videoFilePath);
            CascadeClassifier faceDetector = new CascadeClassifier(getTempFileFrontalFaceXml().toString());

            if (!videoCapture.isOpened()) {
                logger.info("Error opening video.");
                throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), ProblemType.ERROR_FACE_DETECTED.getUri(),
                        ProblemType.ERROR_FACE_DETECTED.getTitle(), "Error opening video");
            }

            Mat frame = new Mat();
            while (videoCapture.read(frame)) {
                /* Detectar rostos no frame */
                RectVector faceDetections = new RectVector();
                faceDetector.detectMultiScale(frame, faceDetections);

                for (Rect rect : faceDetections.get()) {
                    /* Desenhar o retângulo ao redor do rosto ou fazer o reconhecimento facial */
                    opencv_imgproc.rectangle(frame,
                            new Point(rect.x(), rect.y()),
                            new Point(rect.x() + rect.width(), rect.y() + rect.height()),
                            new Scalar(0, 255, 0, 0)  // Cor verde em formato RGBA
                    );

                    logger.info("Rosto detectado em: " + rect);
                    /* Recortar a região do rosto para reconhecimento */
                    Mat face = new Mat(frame, rect);
                    /* Redimensionar para o tamanho esperado pelo modelo treinado */
                    opencv_imgproc.resize(face, face, new Size(200, 200));
                    handlerFaceRecognized(face);

                    opencv_highgui.imshow("Face Detection", frame);
                }
            }
            videoCapture.release();

            return Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("Video processed successfully")
                    .build();
        }catch (Exception e){
            logger.error("Error to process video: " + e.getMessage());
            ProblemType problemType = ProblemType.ERROR_VIDEO_PROCESS;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(),
                    "Error to process video: " + videoFilePath);
        }
    }

    @SneakyThrows
    private Path getTempFileFrontalFaceXml() {
        Path tempFile = Files.createTempFile("haarcascade_frontalface_alt", ".xml");
        try (InputStream xmlStream = xmlResource.getInputStream()) {
            Files.copy(xmlStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Error creating temp file: " + e.getMessage());
            ProblemType problemType = ProblemType.ERROR_FACE_DETECTED;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(),
                    "Error creating temp file frontal face: " + e.getMessage());
        }
        return tempFile;
    }

    public Response recognizeFaceFromVideo(UUID videoId) {
        return null;
    }

    private void handlerFaceRecognized(Mat img) {
        /* Realizar reconhecimento*/
        int[] predictedLabel = new int[1];
        double[] confidence = new double[1];
        faceRecognizer.predict(img, predictedLabel, confidence);
        logger.info("Face detected: " + predictedLabel[0] + " com confidant: " + Arrays.toString(confidence));

        if (confidence[0] < 50.0) {
            String userName = userService.getUserFromLabel(predictedLabel[0]);
            logger.info("Face detected: " + predictedLabel[0] + " com confiança: " + Arrays.toString(confidence));
        } else {
            logger.info("Confidant is low");
        }
    }
}