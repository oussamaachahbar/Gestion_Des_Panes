package com.GestionPanne.GestionPanne.dto;

import lombok.Data;

import java.util.Date;
@Data
public class ProblemDTO {
    private Double id;
    private Double userId;
    private Double postId;
    private Double projectId;
    private Date failureDate;
    private Date solutionDate;
    private String failureDetails;
    private String priority;//(critical,hight,medium,low)
    private String etat;//(En attente : in progress : finished)
}
