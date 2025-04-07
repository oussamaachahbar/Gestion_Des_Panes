package com.GestionPanne.GestionPanne.dto;

import com.GestionPanne.GestionPanne.entity.Etat;
import com.GestionPanne.GestionPanne.entity.Priority;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

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
    private String failureDetails;
    private String priority;
    private String etat;
    private String imageUrl;

}