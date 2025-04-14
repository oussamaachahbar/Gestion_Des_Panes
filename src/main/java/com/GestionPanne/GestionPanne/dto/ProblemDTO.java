package com.GestionPanne.GestionPanne.dto;

import com.GestionPanne.GestionPanne.entity.Etat;
import com.GestionPanne.GestionPanne.entity.Priority;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProblemDTO {
    private Long id;
    private Long userId;
    private Long postId;
    private String username;
    private String postname;
    private String title;
    private LocalDateTime failureDate;
    private LocalDateTime solutionDate;
    private LocalDateTime assignmentDate; // Ajouté
    private String failureDetails;
    private String priority;
    private String etat;
    private String imageUrl;
    private String technicien;
    private String technicienemail;
    private Long technicienId;
}