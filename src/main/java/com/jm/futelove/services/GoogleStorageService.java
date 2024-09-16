package com.jm.futelove.services;

import com.google.cloud.storage.*;
import com.jm.futelove.controllers.GoogleStorageController;
import com.jm.futelove.dto.ImageDTO;
import com.jm.futelove.dto.UserDTO;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class GoogleStorageService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleStorageController.class);

    @Value("${google.cloud.bucketName}")
    private String BUCKET_NAME;
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final UserService userService;
    private final ImageService imageService;

    public GoogleStorageService(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }


    public ImageDTO uploadFile(@RequestParam("file") MultipartFile file,
                               @RequestParam("userId") UUID userId) {
        try {

            UserDTO userDTO = userService.findById(userId);


            logger.info("Starting to uploading file: " + file.getOriginalFilename());
            String fileName = generateFileName(file.getOriginalFilename(), userDTO.getId());
            BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            /* Faz o upload do arquivo */
            storage.create(blobInfo, file.getBytes());

            return imageService.save(ImageDTO.builder()
                    .fileName(fileName)
                    .url("https://storage.cloud.google.com/" + BUCKET_NAME + "/" + fileName)
                    .userId(userDTO.getId())
                    .build());

        } catch (Exception e) {
            logger.info("Error to uploading file: " + e.getMessage());
            ProblemType problemType = ProblemType.ERROR_UPLOAD_FILE;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(),
                    "Error to uploading file: " + e.getMessage());
        }

    }

    public byte[] downloadFile(@PathVariable UUID profileId, @PathVariable String fileName) {
        try {
            Blob blob = storage.get(BlobId.of(BUCKET_NAME, generateFileName(fileName, profileId)));

            if (blob == null || !blob.exists()) {
                return null;    /* Retorna null se o arquivo não existir */
            }

            /* Retorna o arquivo como um array de bytes */
            return blob.getContent();
        } catch (Exception e) {
            logger.info("Error to download file: " + e.getMessage());
            ProblemType problemType = ProblemType.ERROR_UPLOAD_FILE;
            throw new FuteLoveException(HttpStatus.BAD_REQUEST.value(), problemType.getUri(), problemType.getTitle(),
                    "Error to download file: " + e.getMessage());
        }
    }

    private String generateFileName(String originalFileName, UUID profileId) {
        return profileId + "/" + originalFileName;
    }
}
