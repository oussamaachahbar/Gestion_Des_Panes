package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.service.Implementation.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Créer un projet
    @PostMapping
    public Response<ProjectDTO> createProject(@RequestBody Project projectDTO) {
        return projectService.createProject(projectDTO);
    }

    // Récupérer tous les projets
    @GetMapping
    public Response<List<ProjectDTO>> getAllProjects() {
        return projectService.getAllProjects();
    }

    // Récupérer un projet par son ID
    @GetMapping("/{id}")
    public Response<ProjectDTO> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    // Mettre à jour un projet
    @PutMapping("/{id}")
    public Response<ProjectDTO> updateProject( @RequestBody Project projectDTO) {
        return projectService.updateProject(projectDTO);
    }

    // Supprimer un projet
    @DeleteMapping("/{id}")
    public Response<Void> deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }

    // Assigner un poste de travail à un projet
    @PostMapping("/{projectId}/assign-workstation")
    public Response<ProjectDTO> assignWorkstationToProject(
            @PathVariable Long projectId,
            @RequestParam Long workstationId) {
        return projectService.assignWorkstationToProject(projectId, workstationId);
    }
}
