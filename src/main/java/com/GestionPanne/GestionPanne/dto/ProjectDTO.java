package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDTO {
    private long id;
    private String nameProject;
    private Long familyId;
    private Long filiereId;
    private String familyName;
    private String filiereName;
}