package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDTO {
    private long id;
    private String nameProject;
    private long familyId; // Ou FamilyDTO si nécessaire
    private long filiereId; // Ou FiliereDTO si nécessaire
    private List<Long> postIds;
    private List<PostDTO> posts;// IDs des posts
}