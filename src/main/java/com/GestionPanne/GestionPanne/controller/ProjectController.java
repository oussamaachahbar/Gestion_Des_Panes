package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.ProjectRepository;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import com.GestionPanne.GestionPanne.service.Implementation.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LogService logService;

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping("/add/{familleId}/{filiereId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<ProjectDTO>> createProject(
            @PathVariable Long familleId,
            @PathVariable Long filiereId,
            @RequestBody Project projectDTO
    ) {
        Response<ProjectDTO> response;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            response = projectService.createProject(familleId, filiereId, projectDTO);
            logService.saveLog("Projet créé avec succès : '" + projectDTO.getNameProject() + "' par " + email + ".");
        } catch (Exception e) {
            logService.saveLog("Erreur lors de la création du projet : " + e.getMessage());
            response = new Response<>();
            response.setMessage("Erreur lors de la création du projet.");
            response.setStatusCode(500);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<ProjectDTO>>> getAllProjects() {
        Response<List<ProjectDTO>> response = projectService.getAllProjects();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getProjectById/{id}")
    public ResponseEntity<Response<ProjectDTO>> getProjectById(@PathVariable Long id) {
        Response<ProjectDTO> response = projectService.getProjectById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateProject/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<ProjectDTO>> updateProject(@PathVariable Long id, @RequestBody Project projectDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new OurException("Projet introuvable avec l'ID : " + id));

        Response<ProjectDTO> response = projectService.updateProject(id, projectDTO);

        if (response.getStatusCode() == 200) {
            logService.saveLog("Mise à jour réussie : Le projet '" + project.getNameProject() + "' (ID: " + id + ") a été modifié par " + email + ".");
        } else {
            logService.saveLog("Échec de la mise à jour du projet avec l'ID : " + id + ".");
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/deleteProject/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteProject(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new OurException("Projet introuvable avec l'ID : " + id));

        Response<Void> response = projectService.deleteProject(id);

        if (response.getStatusCode() == 200) {
            logService.saveLog("Suppression réussie : Le projet '" + project.getNameProject() + "' (ID: " + id + ") a été supprimé par " + email + ".");
        } else {
            logService.saveLog("Échec de la suppression du projet avec l'ID : " + id + ".");
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/assignWorkstationToProject/{projectId}/assign-workstation")
    public ResponseEntity<Response<ProjectDTO>> assignWorkstationToProject(
            @PathVariable Long projectId,
            @RequestParam Long workstationId) {
        Response<ProjectDTO> response = projectService.assignWorkstationToProject(projectId, workstationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
