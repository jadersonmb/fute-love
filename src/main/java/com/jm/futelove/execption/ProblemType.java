package com.jm.futelove.execption;


import lombok.Getter;

@Getter
public enum ProblemType {


    INVALID_BODY("/invalid-body", "Invalid Body", "invalid_message_body"),
    INVALID_BODY_PARAM("/invalid-body", "Invaled Body", "invalid_message_body_param"),
    INVALID_VALUE_LONG_DATABASE("/invaled-long-database", "Value Long", "invalid_value_long_database"),
    USER_NOT_FOUND("/user-not-found", "User not found", "account_not_exists"),
    USER_NOT_EXISTS("/user-not-exists", "User not exists", "user_not_found"),
    CPF_ALREADY_EXISTS("/cpf-exists", "CPF already exists", "cpf_already_exists"),
    CELL_PHONE_ALREADY_EXISTS("/cellphone-exists", "CellPhone already exists", "cellphone_already_exists"),
    DATE_INVALED("/date-invalid", "Date Invalid", "date_invalid"),
    ERROR_UPLOAD_FILE("/error-upload", "Error to uploading file", "error_upload_file"),
    ERROR_FACE_DETECTED("/error-face-detected", "Error to detected file", "error_detected_file"),
    ERROR_DOWNLOAD_FILE("/error-download", "Error to download file", "error_download_file");

    private String uri;
    private String title;
    private String messageSource;

    ProblemType(String path, String title, String messageSource){
        this.uri = "https://futelove.com" + path;
        this.title = title;
        this.messageSource = messageSource;
    }
}
