package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;
@Data
public class UserDTO {
    private long id;
    private String name;
    private String email;
    //sans password
    private String phone;
    private String role;
    private boolean isSupervisor;
    private long postId;
    private List<ProblemDTO> problems;// Chaque utilisateur est assigné à UN SEUL poste
}
