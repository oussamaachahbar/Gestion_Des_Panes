package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.service.Implementation.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workstations")
public class PostController {

    @Autowired
    private PostService workstationService;

    // Créer un poste de travail
    @PostMapping
    public Response<PostDTO> createWorkstation(@RequestBody Post workstationDTO) {
        return workstationService.createPost(workstationDTO);
    }

    // Récupérer tous les postes de travail
    @GetMapping
    public Response<List<PostDTO>> getAllWorkstations() {
        return workstationService.getAllPosts();
    }

    // Récupérer un poste de travail par son ID
    @GetMapping("/{id}")
    public Response<PostDTO> getWorkstationById(@PathVariable Long id) {
        return workstationService.getPostById(id);
    }

    // Mettre à jour un poste de travail
    @PutMapping("/{id}")
    public Response<PostDTO> updateWorkstation( @RequestBody Post workstationDTO) {
        return workstationService.updatePost( workstationDTO);
    }

    // Supprimer un poste de travail
    @DeleteMapping("/{id}")
    public Response<Void> deleteWorkstation(@PathVariable Long id) {
        return workstationService.deletePost(id);
    }
}
