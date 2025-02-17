package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostDTO {
    private long id;
    private String name;
    private String description;
    private String position;
    private long projectId; // Ou ProjectDTO si nécessaire
    private List<Long> problemIds;
    private List<UserDTO> users;
    private List<ProblemDTO> Problems;// IDs des problèmes
}