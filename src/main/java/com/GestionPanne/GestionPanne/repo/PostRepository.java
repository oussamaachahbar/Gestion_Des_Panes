package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    boolean existsByName(String name);

    Post findById(Long postId);

    boolean existsByProject(Project project);

    // Correction du type ID
}
