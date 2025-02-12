package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostDTO {
    private long id;
    private String name;
    private String description;
    private String position;
    private long projectId; // Objet ProjectDTO au lieu de l'ID
    private List<UserDTO> users;
    private List<ProblemDTO> problems;// Liste des utilisateurs affectés à ce poste
}
