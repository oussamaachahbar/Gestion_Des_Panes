package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDTO {
    private long id;
    private String nameProject;
    private long familyId;  // Objet FamilyDTO au lieu de l'ID
    private long filiereId; // Objet FiliereDTO au lieu de l'ID
    private List<PostDTO> posts; // Liste des postes liés au projet
}
