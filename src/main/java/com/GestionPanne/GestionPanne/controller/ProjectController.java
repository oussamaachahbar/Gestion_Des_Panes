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

    @PostMapping("/add/{familyId}/{programId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<ProjectDTO>> createProject(
            @PathVariable Long familyId,
            @PathVariable Long programId,
            @RequestBody Project projectDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Response<ProjectDTO> response = new Response<>();

        try {
            logService.saveLog("Attempting to create project: " + projectDTO.getNameProject() + " by " + email);

            response = projectService.createProject(familyId, programId, projectDTO);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Project created successfully: '" + projectDTO.getNameProject() + "' by " + email);
            } else {
                logService.saveLog("Failed to create project: " + projectDTO.getNameProject() + ". Reason: " + response.getMessage());
            }

        } catch (Exception e) {
            logService.saveLog("Error creating project: " + e.getMessage() + " by " + email);
            response.setMessage("Error creating project: " + e.getMessage());
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
        logService.saveLog("Fetching project with ID: " + id);
        Response<ProjectDTO> response = projectService.getProjectById(id);

        if(response.getStatusCode() != 200) {
            logService.saveLog("Project not found with ID: " + id);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateProject/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<ProjectDTO>> updateProject(
            @PathVariable Long id,
            @RequestBody Project projectDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));

            logService.saveLog("Attempting to update project: " + project.getNameProject() + " (ID: " + id + ") by " + email);

            Response<ProjectDTO> response = projectService.updateProject(id, projectDTO);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Project updated successfully: '" + project.getNameProject() + "' (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to update project with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error updating project with ID: " + id + ". Error: " + e.getMessage());
            Response<ProjectDTO> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteProject/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteProject(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));

            logService.saveLog("Attempting to delete project: " + project.getNameProject() + " (ID: " + id + ") by " + email);

            Response<Void> response = projectService.deleteProject(id);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Project deleted successfully: '" + project.getNameProject() + "' (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to delete project with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error deleting project with ID: " + id + ". Error: " + e.getMessage());
            Response<Void> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PostMapping("/assignWorkstationToProject/{projectId}/assign-workstation")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<ProjectDTO>> assignWorkstationToProject(
            @PathVariable Long projectId,
            @RequestParam Long workstationId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            logService.saveLog("Attempting to assign workstation ID: " + workstationId + " to project ID: " + projectId + " by " + email);

            Response<ProjectDTO> response = projectService.assignWorkstationToProject(projectId, workstationId);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Workstation assigned successfully to project ID: " + projectId + " by " + email);
            } else {
                logService.saveLog("Failed to assign workstation to project. Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (Exception e) {
            logService.saveLog("Error assigning workstation to project: " + e.getMessage());
            Response<ProjectDTO> errorResponse = new Response<>();
            errorResponse.setMessage("Error assigning workstation: " + e.getMessage());
            errorResponse.setStatusCode(500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}