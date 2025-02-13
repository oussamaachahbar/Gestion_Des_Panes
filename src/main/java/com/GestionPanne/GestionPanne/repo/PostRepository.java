package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    boolean existsByName(String name);

    // Correction du type ID
}
