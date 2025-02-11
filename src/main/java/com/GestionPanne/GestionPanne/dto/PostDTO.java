package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostDTO {
    private Double id;
    private String name;
    private String description;
    private String position;
    private Double projectId; // Objet ProjectDTO au lieu de l'ID
    private List<UserDTO> users;
    private List<ProblemDTO> problems;// Liste des utilisateurs affectés à ce poste
}
