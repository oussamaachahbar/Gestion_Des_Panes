package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDTO {
    private Double id;
    private String nameProject;
    private Double familyId;  // Objet FamilyDTO au lieu de l'ID
    private Double filiereId; // Objet FiliereDTO au lieu de l'ID
    private List<PostDTO> posts; // Liste des postes liés au projet
}
