package com.GestionPanne.GestionPanne.dto;

import com.GestionPanne.GestionPanne.entity.Etat;
import com.GestionPanne.GestionPanne.entity.Priority;
import lombok.Data;
import java.util.Date;
import java.util.List;
@Data
public class ProblemDTO {
    private Long id;
    private Long userId;
    private Long postId;
    private String username;
    private String postname;// ID de l'utilisateur
    private Date failureDate;
    private Date solutionDate;
    private String failureDetails;
    private String priority; // Enum Priority converti en String
    private String etat; // Enum Etat converti en String

}