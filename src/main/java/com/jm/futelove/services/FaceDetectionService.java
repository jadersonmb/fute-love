package com.jm.futelove.services;

import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.jm.futelove.commons.Commons;
import com.jm.futelove.commons.FlickrDownloader;
import com.jm.futelove.commons.VideoFrameDisplay;
import com.jm.futelove.controllers.GoogleStorageController;
import com.jm.futelove.entity.Image;
import com.jm.futelove.entity.User;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.ProblemType;
import com.jm.futelove.execption.Response;
import lombok.SneakyThrows;
import org.apache.logging.log4j.util.Strings;
import org.bytedeco.opencv.global.opencv_core;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FaceDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleStorageController.class);

    @Value("classpath:opencv/haarcascades/haarcascade_frontalface_alt.xml")
    private Resource xmlResource;
    @Value("${google.cloud.bucketName}")
    private String BUCKET_NAME;

    private LBPHFaceRecognizer faceRecognizer;

    private final UserService userService;
    private final ImageService imageService;
    private final VideoFrameDisplay frameDisplay = new VideoFrameDisplay();
    private final FlickrDownloader flickrDownloader = new FlickrDownloader();

    public FaceDetectionService(UserService userService, ImageService imageService) {
        this.imageService = imageService;
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

            frameDisplay.showInFrame(image);

            return Response.builder().status(HttpStatus.OK.value()).message("Faces detectable: " + faceDetections.size()).build();

        } catch (Exception e) {
            logger.info("Error to process file: " + e.getMessage());
            ProblemType problemType = ProblemType.ERROR_FACE_DETECTED;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(), "Error to process file: " + file.getOriginalFilename());
        }
    }

    public Response recognizeImage(@RequestParam("file") MultipartFile file) throws IOException {
        this.faceRecognizer.read("user_model_Neymar.xml");
        /* Realizar reconhecimento*/
        handlerFaceRecognized(opencv_imgcodecs.imdecode(new Mat(file.getBytes()), opencv_imgcodecs.IMREAD_GRAYSCALE), new Rect(), new Mat());
        return Response.
                builder()
                .status(HttpStatus.OK.value())
                .message("Face recognized successfully").build();
    }

    public Response processVideo(MultipartFile videoFilePath) {
        try {
            this.faceRecognizer.read("user_model_Neymar.xml");

            Path tempVideoPath = Files.createTempFile("neymar", ".mp4"); // ou outra extensão de vídeo apropriada
            Files.write(tempVideoPath, videoFilePath.getBytes());

            /* Abrir o vídeo */
            VideoCapture videoCapture = new VideoCapture(tempVideoPath.toString());
            CascadeClassifier faceDetector = new CascadeClassifier(getTempFileFrontalFaceXml().toString());

            if (!videoCapture.isOpened()) {
                logger.info("Error opening video.");
                throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), ProblemType.ERROR_FACE_DETECTED.getUri(), ProblemType.ERROR_FACE_DETECTED.getTitle(), "Error opening video");
            }

            Mat frame = new Mat();
            Mat grayFrame = new Mat();
            while (videoCapture.read(frame)) {
                /* Converte a imagem para grayscale e adiciona à lista de imagens*/
                opencv_imgproc.cvtColor(frame, grayFrame, opencv_imgproc.COLOR_BGR2GRAY);

                /* Detectar rostos no frame */
                RectVector faceDetections = new RectVector();
                faceDetector.detectMultiScale(grayFrame, faceDetections);

                for (Rect rect : faceDetections.get()) {
                    /* Recortar a região do rosto para reconhecimento
                     * logger.info("Face detected: " + rect); */
                    Mat face = new Mat(grayFrame, rect);

                    /* Redimensionar para o tamanho esperado pelo modelo treinado */
                    opencv_imgproc.resize(face, face, new Size(200, 200));
                    handlerFaceRecognized(face, rect, frame);
                    /*opencv_highgui.imshow("Face Detection", frame);*/
                }
                frameDisplay.showInFrame(frame);
            }
            videoCapture.release();

            return Response.builder().status(HttpStatus.OK.value()).message("Video processed successfully").build();
        } catch (Exception e) {
            logger.error("Error to process video: " + e.getMessage());
            ProblemType problemType = ProblemType.ERROR_VIDEO_PROCESS;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(), "Error to process video: " + videoFilePath);
        }
    }

    @SneakyThrows
    public Response trainModel(UUID userId) {
        User user = userService.findEntityById(userId);
        int hashCodeUser = user.getId().hashCode();
        List<Image> userImages = imageService.findByUserId(userId);

        if (userImages.isEmpty()) {
            ProblemType problemType = ProblemType.IMAGE_NOT_FOUND;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getTitle(), problemType.getUri(), "Image not found for user: " + user.getId());
        }

        List<Mat> imageMats = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        /*getAllImgByUserForTrain(user, imageMats, labels);*/
        /*getImgFromInternet("neymar", imageMats, labels, user);*/

        for (Image img : userImages) {
            Mat imageMat = opencv_imgcodecs.imdecode(new Mat(Commons.downloadImageStorageByFileName(BUCKET_NAME, img.getFileName())), opencv_imgcodecs.IMREAD_GRAYSCALE);

            Mat resizedMat = new Mat();
            opencv_imgproc.resize(imageMat, resizedMat, new Size(200, 200));

            imageMats.add(resizedMat);
            labels.add(user.getId().hashCode());
        };

        user.setHashCode(hashCodeUser);
        userService.updateUserEntity(user);

        /* Converter a lista de imagens para MatVector*/
        MatVector images = new MatVector(imageMats.size());
        for (int i = 0; i < imageMats.size(); i++) {
            images.put(i, imageMats.get(i));
        }

        /* Converter a lista de labels para um Mat do tipo CV_32SC1 (inteiros de 32 bits)*/
        Mat labelsMat = new Mat(labels.size(), 1, opencv_core.CV_32SC1);
        for (int i = 0; i < labels.size(); i++) {
            labelsMat.ptr(i).putInt(labels.get(i));
        }

        faceRecognizer.train(images, labelsMat);

        saveTrainedModel(user.getName());

        return Response.builder().status(HttpStatus.OK.value()).message("Model training successfully for user: " + user.getName()).build();
    }

    private void getImgFromInternet(String query, List<Mat> imageMats, List<Integer> labels, User user) {
        PhotoList<Photo> photos = flickrDownloader.getImageWithQuery(query, 50);
        photos.forEach(photo -> {
            try {
                Mat imageMat = opencv_imgcodecs.imdecode(new Mat(flickrDownloader.downloadImageAsBytes(photo.getMediumUrl())), opencv_imgcodecs.IMREAD_GRAYSCALE);
                Mat resizedMat = new Mat();
                opencv_imgproc.resize(imageMat, resizedMat, new Size(200, 200));

                imageMats.add(imageMat);
                labels.add(user.getId().hashCode());
            } catch (Exception e) {
                logger.error("Error to download image: " + e.getMessage());
            }
        });
    }

    public Response recognizeFaceFromVideo(MultipartFile videoFilePath) {
        return processVideo(videoFilePath);
    }

    private void handlerFaceRecognized(Mat img, Rect rect, Mat frame) {
        /* Realizar reconhecimento*/
        int[] predictedLabel = new int[1];
        double[] confidence = new double[1];
        faceRecognizer.predict(img, predictedLabel, confidence);
        if (confidence[0] > 50.0) {
            opencv_imgproc.rectangle(frame, new Point(rect.x(), rect.y()), new Point(rect.x() + rect.width(), rect.y() + rect.height()), new Scalar(0, 255, 0, 0));
            User userName = userService.getUserFromLabel(predictedLabel[0]);
            logger.info("Face detected: " + userName.getName() + " with confidant: " + confidence[0]);
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
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(), "Error creating temp file frontal face: " + e.getMessage());
        }
        return tempFile;
    }


    private void getAllImgByUserForTrain(User user, List<Mat> imageMats, List<Integer> labels) throws IOException {
        Commons.downloadImageStorageByFolder(BUCKET_NAME, user.getId().toString()).forEach(imageBytes -> {
            Mat imageMat = opencv_imgcodecs.imdecode(new Mat(imageBytes), opencv_imgcodecs.IMREAD_GRAYSCALE);
            /* Redimensionar para um tamanho fixo (exemplo: 200x200) */
            Mat resizedMat = new Mat();
            opencv_imgproc.resize(imageMat, resizedMat, new Size(200, 200));

            /*frameDisplay.showInFrame(imageMat);*/

            imageMats.add(imageMat);
            labels.add(user.getId().hashCode());
        });
    }

    private void saveTrainedModel(String userName) {
        String filePath = "user_model_" + Strings.toRootLowerCase(userName) + ".xml";
        faceRecognizer.save(filePath);
    }
}