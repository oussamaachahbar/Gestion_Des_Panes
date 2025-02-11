package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ProblemDTO {
    private Double id;
    private Double user;    // L'utilisateur qui signale la panne
    private Date failureDate;
    private Date solutionDate;
    private String failureDetails;
    private String priority; // (critical, high, medium, low)
    private String etat; // (En attente, in progress, finished)
    private List<Double> post;//Peut avoir plusieurs post qui sont en panne
}
