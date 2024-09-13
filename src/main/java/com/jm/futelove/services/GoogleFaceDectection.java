package com.jm.futelove.services;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleFaceDectection {

    public String analyzeImage(MultipartFile file) throws Exception {
        /* Converter o arquivo em ByteString */
        ByteString imgBytes = ByteString.copyFrom(file.getBytes());

        /* Construir a imagem a partir do byte string */
        Image img = Image.newBuilder().setContent(imgBytes).build();

        /* Configurar o pedido de detecção facial */
        Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        /* Inicializar o cliente Vision */
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    throw new Exception("Error to analyze image: " + res.getError().getMessage());
                }

                /* Processar os resultados de detecção facial */
                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    /* Por exemplo, pegar as coordenadas do rosto ou emoções detectadas */
                    System.out.printf("Face detected at: %s\n", annotation.getBoundingPoly());
                }
            }
        }
        return "Faces detected!";
    }
}
