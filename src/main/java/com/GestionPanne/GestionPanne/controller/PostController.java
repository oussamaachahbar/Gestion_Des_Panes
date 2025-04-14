package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.PostDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Post")
public class PostController {

    @Autowired
    private PostService postService;

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
    public ResponseEntity<Response<PostDTO>> createPost(@RequestBody Post postDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Response<PostDTO> response = postService.createPost(postDTO);

        if (response.getStatusCode() == 201) {
            logService.saveLog("Workstation created successfully: " + postDTO.getName() + " by " + email);
        } else {
            logService.saveLog("Failed to create workstation. Reason: " + response.getMessage());
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<PostDTO>>> getAllPosts() {
        Response<List<PostDTO>> response = postService.getAllPosts();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getWorkstationById/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response<PostDTO>> getPostById(@PathVariable Long id) {
        logService.saveLog("Fetching workstation with ID: " + id);
        Response<PostDTO> response = postService.getPostById(id);

        if (response.getStatusCode() != 200) {
            logService.saveLog("Workstation not found with ID: " + id);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getWorkstationByUserId")
    public ResponseEntity<Response<PostDTO>> getPostByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logService.saveLog("Fetching workstation for user: " + email);
        Response<PostDTO> response = postService.getPostByUserId();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateWorkstation/{postId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Post>> updatePost(@PathVariable Long postId, @RequestBody Post updatedPost) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<Post> optionalPost = Optional.ofNullable(postRepository.findById(postId));
        if (!optionalPost.isPresent()) {
            logService.saveLog("Attempted to update non-existent workstation with ID: " + postId);
            return ResponseEntity.notFound().build();
        }

        logService.saveLog("Attempting to update workstation with ID: " + postId + " by " + email);

        Post existingPost = optionalPost.get();
        existingPost.setName(updatedPost.getName());
        existingPost.setDescription(updatedPost.getDescription());
        existingPost.setPosition(updatedPost.getPosition());

        // Update project association
        if (updatedPost.getProject() != null) {
            Project project = projectRepository.findById(updatedPost.getProject().getId()).orElse(null);
            existingPost.setProject(project);
        } else {
            existingPost.setProject(null);
        }

        // Update user associations
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
        Response<Post> response = new Response<>();
        response.setData(savedPost);
        response.setMessage("Workstation updated successfully");
        response.setStatusCode(200);

        logService.saveLog("Workstation updated successfully: " + savedPost.getName() + " (ID: " + postId + ") by " + email);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteWorkstation/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Post post = postRepository.findById(id);

        if (post == null) {
            logService.saveLog("Attempted to delete non-existent workstation with ID: " + id);
            return ResponseEntity.notFound().build();
        }

        logService.saveLog("Attempting to delete workstation: " + post.getName() + " (ID: " + id + ") by " + email);

        Response<Void> response = postService.deletePost(id);

        if (response.getStatusCode() == 200) {
            logService.saveLog("Workstation deleted successfully: " + post.getName() + " (ID: " + id + ") by " + email);
        } else {
            logService.saveLog("Failed to delete workstation with ID: " + id + ". Reason: " + response.getMessage());
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}