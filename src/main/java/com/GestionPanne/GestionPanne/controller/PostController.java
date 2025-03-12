package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import com.GestionPanne.GestionPanne.service.Implementation.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Post")
public class PostController {

    @Autowired
    private PostService workstationService;

    @Autowired
    private LogService logService;  // Ajout du LogService

    @PostMapping("/add/{project}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<PostDTO>> createWorkstation(@PathVariable Long project, @RequestBody Post workstationDTO) {
        Response<PostDTO> response = workstationService.createPost(project, workstationDTO);

        if (response.getStatusCode() == 201) {
            logService.saveLog("Poste de travail créé pour le projet ID : " + project);
        } else {
            logService.saveLog("Échec de la création du poste de travail pour le projet ID : " + project);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")

    public ResponseEntity<Response<List<PostDTO>>> getAllWorkstations() {

        Response<List<PostDTO>> response=  workstationService.getAllPosts();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getWorkstationById/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")

    public ResponseEntity<Response<PostDTO>> getWorkstationById(@PathVariable Long id) {
        Response<PostDTO> response= workstationService.getPostById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/getWorkstationByUserId")
    public ResponseEntity<Response<PostDTO>> getWorkstationByUserId() {
        Response<PostDTO> response= workstationService.getPostByUserId();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PutMapping("/updateWorkstation/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<PostDTO>> updateWorkstation(@PathVariable Long id ,@RequestBody Post workstationDTO) {
        Response<PostDTO> response= workstationService.updatePost(id, workstationDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }




    @DeleteMapping("/deleteWorkstation/{id}")
    public ResponseEntity<Response<Void>> deleteWorkstation(@PathVariable Long id) {

        Response<Void> response= workstationService.deletePost(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
