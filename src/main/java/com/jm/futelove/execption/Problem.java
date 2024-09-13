package com.jm.futelove.execption;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Problem {

    private Integer status;
    private String type;
    private String title;
    private String details;

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