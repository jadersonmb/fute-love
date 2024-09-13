package com.jm.futelove.execption;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private Integer status;
    private String message;

    private String userMessage;
    private String uri;
    private List<Field> fields;


    @Getter
    @Builder
    public static class Field {
        private String name;
        private String userMessage;
    }

}