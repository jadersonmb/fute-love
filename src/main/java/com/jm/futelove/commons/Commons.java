package com.jm.futelove.commons;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Commons {

    private static final Logger logger = LoggerFactory.getLogger(Commons.class);

    public static byte[] downloadImageStorageByFileName(String bucketName, String objectName) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(bucketName, objectName);
        if (blob == null) {
            throw new IOException("The file was not found in the bucket.");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        blob.downloadTo(baos);
        return baos.toByteArray();
    }

    public static List<byte[]> downloadImageStorageByFolder(String bucketName, String folderName) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        List<byte[]> images = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        String prefix = folderName.endsWith("/") ? folderName : folderName + "/";

        for (Blob blob : bucket.list(Storage.BlobListOption.prefix(prefix)).iterateAll()) {
            if (!blob.isDirectory()) {
                logger.info("Downloading: " + blob.getName());
                byte[] imageBytes = blob.getContent();
                images.add(imageBytes);
            }
        }
        return images;
    }
}
