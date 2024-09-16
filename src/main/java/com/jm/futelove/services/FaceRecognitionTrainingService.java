package com.jm.futelove.services;

import com.jm.futelove.commons.Commons;
import com.jm.futelove.controllers.GoogleStorageController;
import com.jm.futelove.entity.Image;
import com.jm.futelove.entity.User;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.ProblemType;
import com.jm.futelove.execption.Response;
import com.jm.futelove.repository.ImageRepository;
import com.jm.futelove.repository.UserRepository;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FaceRecognitionTrainingService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleStorageController.class);
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    private LBPHFaceRecognizer recognizer;

    public FaceRecognitionTrainingService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        recognizer = LBPHFaceRecognizer.create();
    }

    public Response trainModel(UUID userId) throws FuteLoveException {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            ProblemType problemType = ProblemType.USER_NOT_FOUND;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(),
                    problemType.getTitle(), problemType.getUri(), "User not found");
        }

        User user = userOpt.get();

        List<Image> userImages = imageRepository.findByUserId(userId);

        if (userImages.isEmpty()) {
            ProblemType problemType = ProblemType.IMAGE_NOT_FOUND;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(),
                    problemType.getTitle(), problemType.getUri(), "Image not found for user: " + user.getId());
        }

        List<Mat> imageMats = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (Image img : userImages) {
            /* Carregar a imagem do Google Cloud Storage */
            Mat imageMat = loadImageFromStorage(img.getUrl());


            /* Converter para escala de cinza */
            Mat grayMat = new Mat();
            opencv_imgproc.cvtColor(imageMat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);

            /* Redimensionar para um tamanho fixo (exemplo: 200x200) */
            Mat resizedMat = new Mat();
            opencv_imgproc.resize(imageMat, resizedMat, new Size(200, 200));

            /* Adicionar à lista de imagens e ao label (o ID único do usuário) */
            imageMats.add(resizedMat);
            labels.add(img.getUser().getName());  // O ID é convertido em inteiro para ser usado como label
        }

        /* Converter a lista de imagens para MatVector*/
        MatVector images = new MatVector(imageMats.size());
        for (int i = 0; i < imageMats.size(); i++) {
            images.put(i, imageMats.get(i));
        }

        /* Converter a lista de labels para um Mat do tipo CV_32SC1 (inteiros de 32 bits)*/
        Mat labelsMat = new Mat(labels.size(), 1, opencv_core.CV_32SC1);
        for (int i = 0; i < labels.size(); i++) {
            labelsMat.ptr(i).putString(labels.get(i));
        }

        recognizer.train(images, labelsMat);

        saveTrainedModel(user.getName());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Model training successfully for user: " + user.getName())
                .build();
    }

    private Mat loadImageFromStorage(String url) {
        /* Aqui você faria o download da imagem do Google Cloud Storage e a carregaria no OpenCV */
        return Commons.convertMatOpenCoreCV(Imgcodecs.imread(url));
    }

    private void saveTrainedModel(String userName) {
        String filePath = "trained_models/user_model_" + userName + ".xml";
        recognizer.save(filePath);
    }


}