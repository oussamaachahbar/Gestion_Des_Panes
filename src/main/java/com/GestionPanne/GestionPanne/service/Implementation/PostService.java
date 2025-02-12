package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.User;
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
    PostRepository postRepository;

    @Override
    public Response createPost(Post postRequest) {
        Response response = new Response();
        try {
            Post savedPost = postRepository.save(postRequest);
            PostDTO postDTO = Utils.mapPostEntityToPostDTO(savedPost);
            response.setStatusCode(200);
            response.setPost(postDTO);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding post: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllPosts() {
        Response response = new Response();
        try {
            List<Post> posts = postRepository.findAll();
            List<PostDTO> postDTOList = Utils.mapPostListEntityToPostListDTO(posts);
            response.setStatusCode(200);
            response.setPostList(postDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving posts: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getPostById(Long postId) {
        Response response = new Response();
        try {
            Post post = postRepository.findById(Math.toIntExact(postId)).orElseThrow(() -> new OurException("Post not found"));
            PostDTO postDTO = Utils.mapPostEntityToPostDTOWithProblems(post);
            response.setStatusCode(200);
            response.setPost(postDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving post: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updatePost( Post postRequest) {
        Response response = new Response();
        try {
            if (!postRepository.existsById(Math.toIntExact(postRequest.getId()))){
                throw new OurException("post not found");
            }

            Post updatedPOST = postRepository.save(postRequest);
            PostDTO postDTO = Utils.mapPostEntityToPostDTO(updatedPOST);
            response.setStatusCode(200);
            response.setPost(postDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating post: " + e.getMessage());
        }
        return response;    }

    @Override
    public Response deletePost(Long postId) {
        Response response = new Response();
        try {
            postRepository.findById(Math.toIntExact(postId)).orElseThrow(() -> new OurException("Post not found"));
            postRepository.deleteById(Math.toIntExact(postId));
            response.setStatusCode(200);
            response.setMessage("Post deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting post: " + e.getMessage());
        }
        return response;
    }
}
