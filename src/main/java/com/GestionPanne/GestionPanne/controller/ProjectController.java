package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.service.Implementation.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Créer un projet
    @PostMapping
    public ResponseEntity<Response<ProjectDTO>> createProject(@RequestBody Project projectDTO) {
        Response<ProjectDTO> response= projectService.createProject(projectDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    // Récupérer tous les projets
    @GetMapping
    public ResponseEntity<Response<List<ProjectDTO>>> getAllProjects() {
        Response<List<ProjectDTO>> response= projectService.getAllProjects();
        return ResponseEntity.status(response.getStatusCode()).body(response);


    }

    // Récupérer un projet par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProjectDTO>> getProjectById(@PathVariable Long id) {
        Response<ProjectDTO> response= projectService.getProjectById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    // Mettre à jour un projet
    @PutMapping("/{id}")
    public ResponseEntity<Response<ProjectDTO>> updateProject( @RequestBody Project projectDTO) {
        Response<ProjectDTO> response= projectService.updateProject(projectDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Supprimer un projet
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteProject(@PathVariable Long id) {
            Response<Void> response= projectService.deleteProject(id);
            return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Assigner un poste de travail à un projet
    @PostMapping("/{projectId}/assign-workstation")
    public ResponseEntity<Response<ProjectDTO>> assignWorkstationToProject(
            @PathVariable Long projectId,
            @RequestParam Long workstationId) {
        Response<ProjectDTO> response= projectService.assignWorkstationToProject(projectId, workstationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
