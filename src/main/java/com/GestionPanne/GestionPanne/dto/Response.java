package com.GestionPanne.GestionPanne.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private int statusCode;
    private String message;

    private String token;
    private boolean supervisor;
    private String role;
    private boolean status;
    private String expirationTime;
    private T data;
    private List<T> dataList = new ArrayList<>();


    public Response(int i, String s, Object o) {
    }

    public Response() {

    }
}