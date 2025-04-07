package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.ProjectRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import com.GestionPanne.GestionPanne.service.Implementation.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Post")
public class PostController {

    @Autowired
    private PostService workstationService;

    @Autowired
    private LogService logService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<PostDTO>> createWorkstation( @RequestBody Post workstationDTO) {
        Response<PostDTO> response = workstationService.createPost( workstationDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        if (response.getStatusCode() == 201) {
            logService.saveLog("Poste de travail créé sous le nom : "+workstationDTO.getName()+"  par  "+email);
        } else {
            logService.saveLog("Échec de la création du poste de travail" );
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
    @PutMapping("/updateWorkstation/{postId}")
    public ResponseEntity<Post> updateWorkstation(@PathVariable Long postId, @RequestBody Post updatedPost) {
        Optional<Post> optionalPost = Optional.ofNullable(postRepository.findById(postId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Post post = postRepository.findById(postId);
        Response<Void> response = new Response<>();

        if (!optionalPost.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Post existingPost = optionalPost.get();

        existingPost.setName(updatedPost.getName());
        existingPost.setDescription(updatedPost.getDescription());
        existingPost.setPosition(updatedPost.getPosition());

        if (updatedPost.getProject() != null) {
            Project project = projectRepository.findById(updatedPost.getProject().getId())
                    .orElse(null);
            existingPost.setProject(project);
        } else {
            existingPost.setProject(null);
        }

        if (updatedPost.getUsers() != null && !updatedPost.getUsers().isEmpty()) {
            List<User> users = new ArrayList<>();
            for (User user : updatedPost.getUsers()) {
                User existingUser = userRepository.findById(user.getId()).orElse(null);
                if (existingUser != null) {
                    users.add(existingUser);
                }
            }
            existingPost.setUsers(users);
        }

        Post savedPost = postRepository.save(existingPost);
        response.setMessage("Post Updated Successfully");

        logService.saveLog("Le post avec le nom : " + post.getName() + " avec ID : " + postId + " a été modifié par " + email);

        return ResponseEntity.ok(savedPost);
    }






    @DeleteMapping("/deleteWorkstation/{id}")
    public ResponseEntity<Response<Void>> deleteWorkstation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Post post =postRepository.findById(id);
        Response<Void> response= workstationService.deletePost(id);
        if (response.getStatusCode() == 200) {
            logService.saveLog("La Post : "+ post.getName()+" avec ID : "+id+"  à été modifiée par "+email);
        } else {
            logService.saveLog("Échec de la suppression de post ");
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
