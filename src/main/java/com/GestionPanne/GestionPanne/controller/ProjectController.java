package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.service.Implementation.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/add/{familleId}/{filiereId}")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<Response<ProjectDTO>> createProject(@PathVariable Long familleId,@PathVariable Long filiereId,@RequestBody Project projectDTO) {
        Response<ProjectDTO> response= projectService.createProject(familleId,filiereId,projectDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<ProjectDTO>>> getAllProjects() {
        Response<List<ProjectDTO>> response= projectService.getAllProjects();
        return ResponseEntity.status(response.getStatusCode()).body(response);


    }

    @GetMapping("/getProjectById/{id}")
    public ResponseEntity<Response<ProjectDTO>> getProjectById(@PathVariable Long id) {
        Response<ProjectDTO> response= projectService.getProjectById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PutMapping("/updateProject/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<ProjectDTO>> updateProject(@PathVariable Long id, @RequestBody Project projectDTO) {
        Response<ProjectDTO> response= projectService.updateProject(id,projectDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/deleteProject/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<Response<Void>> deleteProject(@PathVariable Long id) {
            Response<Void> response= projectService.deleteProject(id);
            return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/assignWorkstationToProject/{projectId}/assign-workstation")
    public ResponseEntity<Response<ProjectDTO>> assignWorkstationToProject(
            @PathVariable Long projectId,
            @RequestParam Long workstationId) {
        Response<ProjectDTO> response= projectService.assignWorkstationToProject(projectId, workstationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
