package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostDTO {
    private long id;
    private String name;
    private String description;
    private String position;
    private Long projectId;
    private Long userId;
    private List<Long> problemIds;
    private String projectName;
    private List<UserDTO> users;
    private List<ProblemDTO> Problems;
}