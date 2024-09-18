package com.jm.futelove.commons;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FlickrDownloader {

    private static final String FLICKR_API_KEY = "c2b764de47675e9cca91658845ba1cac";
    private static final String FLICKR_API_SECRET = "25a803173cf4e397";

    private final Flickr flickr;

    public FlickrDownloader() {
        this.flickr = new Flickr(FLICKR_API_KEY, FLICKR_API_SECRET, new REST());
    }

    public PhotoList<Photo> searchPhotos(String query, int count) throws Exception {
        PhotosInterface photos = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();
        params.setText(query);
        params.setMedia("photos");

        return photos.search(params, count, 0);
    }

    public byte[] downloadImageAsBytes(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream in = connection.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    public PhotoList<Photo> getImageWithQuery(String query, int count) {
        try {
            FlickrDownloader downloader = new FlickrDownloader();
            return downloader.searchPhotos(query, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PhotoList<>();
    }
}
