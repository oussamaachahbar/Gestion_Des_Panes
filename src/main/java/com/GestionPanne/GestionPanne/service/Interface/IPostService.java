package com.GestionPanne.GestionPanne.service.Interface;
import java.util.List;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Post;
public interface IPostService {
    Response createPost( Post postRequest);

    Response getAllPosts();

    Response getPostById(Long postId);

    Response updatePost( Post postRequest);

    Response deletePost(Long postId);


}
