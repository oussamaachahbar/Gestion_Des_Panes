package com.GestionPanne.GestionPanne.service.Interface;
import java.util.List;

import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Post;
public interface IPostService {
    Response<PostDTO> createPost(Post postRequest);

    Response<List<PostDTO>> getAllPosts();

    Response<PostDTO> getPostById(Long postId);

    Response<PostDTO> updatePost(Long post_id, Post postRequest);

    Response<Void> deletePost(Long postId);


    Response<PostDTO> getPostByUserId();

}
