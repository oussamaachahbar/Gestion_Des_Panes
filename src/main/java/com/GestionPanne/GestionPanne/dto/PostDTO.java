package com.GestionPanne.GestionPanne.dto;

import lombok.Data;

@Data
public class PostDTO {
    private Double id;
    private String name;
    private String description;
    private String position;
    private Double projectId;
    private Double userId;

}
