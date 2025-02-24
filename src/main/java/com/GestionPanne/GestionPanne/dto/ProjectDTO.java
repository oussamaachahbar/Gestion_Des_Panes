package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDTO {
    private long id;
    private String nameProject;
    private long familyId; // Ou FamilyDTO si nécessaire
    private long filiereId;
    private String familyName;
    private String filiereName;// Ou FiliereDTO si nécessaire
}