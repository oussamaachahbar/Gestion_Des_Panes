package com.GestionPanne.GestionPanne.dto;

import lombok.Data;
import java.util.List;
@Data
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean isSupervisor;
    private long postId; // Ou PostDTO si nécessaire
    private List<Long> problemIds;
    private List<ProblemDTO> problems; // Liste de problèmes
}
