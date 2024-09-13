package com.jm.futelove.execption;


import lombok.Getter;

@Getter
public enum ReponseType {


    FACE_DETECTED("/detect", "Faces detectable", "faces_detectable");

    private String uri;
    private String title;
    private String messageSource;

    ReponseType(String path, String title, String messageSource){
        this.uri = "https://futelove.com" + path;
        this.title = title;
        this.messageSource = messageSource;
    }
}
