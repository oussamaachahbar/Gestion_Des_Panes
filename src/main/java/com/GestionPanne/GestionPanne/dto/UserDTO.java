package com.GestionPanne.GestionPanne.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclure les champs NULL dans la réponse JSON
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean supervisor; // ✅ Correction du nom (évite le préfixe "is" pour un attribut)
    private Long postId;
    private String postName;
    private PostDTO postDTO;// ID du poste de l'utilisateur (ou PostDTO si nécessaire)
    private List<Long> problemIds; // Liste des IDs des problèmes
    private List<ProblemDTO> problems; // Liste complète des problèmes
}
