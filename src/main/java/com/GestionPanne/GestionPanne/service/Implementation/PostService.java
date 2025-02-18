package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.ProjectRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.service.Interface.IPostService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService implements IPostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Override
    public Response<PostDTO> createPost(Long project_id, Post postRequest) {
        Response<PostDTO> response = new Response<>();
        try {


            if (project_id == null) {
                throw new OurException("Project ID cannot be null.");
            }

            // Vérifier si le postRequest contient déjà un projet assigné
            if (postRequest.getProject() != null) {
                throw new OurException("This post is already assigned to a project and cannot be reassigned.");
            }


            // Vérifier si le projet existe
            Project project = projectRepository.findById(project_id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + project_id));




            // Associer le projet au post
            postRequest.setProject(project);
            if (postRequest.getUsers() == null) {
                postRequest.setUsers(new ArrayList<>());
            }
            
            if (postRequest.getProblems() == null) {
                postRequest.setProblems(new ArrayList<>());
            }

            // Sauvegarde du post
            Post savedPost = postRepository.save(postRequest);
            PostDTO postDTO = Utils.mapPostEntityToPostDTO(savedPost);

            // Construire la réponse
            response.setStatusCode(201); // 201 Created
            response.setMessage("Post created successfully");
            response.setData(postDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error adding post: " + e.getMessage());
        }
        return response;
    }




    @Override
    public Response<List<PostDTO>> getAllPosts() {
        Response<List<PostDTO>> response = new Response<>();
        try {
            // Récupérer tous les posts
            List<Post> posts = postRepository.findAll();
            List<PostDTO> postDTOList = Utils.mapPostListEntityToPostListDTO(posts);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Posts retrieved successfully");
            response.setData(postDTOList); // Utilisation de setData pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving posts: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<PostDTO> getPostById(Long postId) {
        Response<PostDTO> response = new Response<>();
        try {
            // Récupérer le post par son ID
            Post post = postRepository.findById(Math.toIntExact(postId))
                    .orElseThrow(() -> new OurException("Post not found with ID: " + postId));
            PostDTO postDTO = Utils.mapPostEntityToPostDTOWithProblems(post);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Post retrieved successfully");
            response.setData(postDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving post: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<PostDTO> updatePost(Long postId, Post postRequest) {
        Response<PostDTO> response = new Response<>();
        try {
            Post existingPost = postRepository.findById(postId);

            if (postRequest.getName() != null) {
                existingPost.setName(postRequest.getName());
            }
            if (postRequest.getDescription() != null) {
                existingPost.setDescription(postRequest.getDescription());
            }
            if (postRequest.getPosition() != null) {
                existingPost.setPosition(postRequest.getPosition());
            }
            if (postRequest.getProject() != null) {
                existingPost.setProject(postRequest.getProject());
            }
            if (postRequest.getUsers() != null) {
                existingPost.setUsers(postRequest.getUsers());
            }
            if (postRequest.getProblems() != null) {
                existingPost.setProblems(postRequest.getProblems());
            }

            Post updatedPost = postRepository.save(existingPost);
            PostDTO postDTO = Utils.mapPostEntityToPostDTO(updatedPost);

            response.setStatusCode(200);
            response.setMessage("Post updated successfully");
            response.setData(postDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating post: " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<Void> deletePost(Long postId) {
        Response<Void> response = new Response<>();
        try {
            // Vérifier si le post existe
            Post post = postRepository.findById(Math.toIntExact(postId))
                    .orElseThrow(() -> new OurException("Post not found with ID: " + postId));
            List<User> users = userRepository.findByPost(post);
            for (User user : users) {
                user.setPost(null); // Supprimer l'association avec le projet
                userRepository.save(user); // Mettre à jour la base de données
            }


            postRepository.delete(post);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Post deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error deleting post: " + e.getMessage());
        }
        return response;
    }
}