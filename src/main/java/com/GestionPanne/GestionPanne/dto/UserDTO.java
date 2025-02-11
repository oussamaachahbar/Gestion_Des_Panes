package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;
@Data
public class UserDTO {
    private Double id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean isSupervisor;
    private Double postId;
    private List<ProblemDTO> problems;// Chaque utilisateur est assigné à UN SEUL poste
}
