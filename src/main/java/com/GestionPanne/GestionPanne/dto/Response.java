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
    private String role;
    private String expirationTime;
    private T data; // Donnée unique (UserDTO, PostDTO, etc.)
    private List<T> dataList = new ArrayList<>(); // Liste de données (ex: List<UserDTO>, List<ProjectDTO>, etc.)


}