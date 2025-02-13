package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.service.Interface.IPostService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public Response<PostDTO> createPost(Post postRequest) {
        Response<PostDTO> response = new Response<>();
        try {
            // Vérifier si le post existe déjà par son nom
            if (postRepository.existsByName(postRequest.getName())) {
                throw new OurException("Post with name '" + postRequest.getName() + "' already exists.");
            }

            // Sauvegarder le nouveau post
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
    public Response<PostDTO> updatePost(Post postRequest) {
        Response<PostDTO> response = new Response<>();
        try {
            // Vérifier si le post existe
            if (!postRepository.existsById(Math.toIntExact(postRequest.getId()))) {
                throw new OurException("Post not found with ID: " + postRequest.getId());
            }

            // Mettre à jour le post
            Post updatedPost = postRepository.save(postRequest);
            PostDTO postDTO = Utils.mapPostEntityToPostDTO(updatedPost);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Post updated successfully");
            response.setData(postDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
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

            // Supprimer le post
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