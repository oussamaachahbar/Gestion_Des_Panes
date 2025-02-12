package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Etat;
import com.GestionPanne.GestionPanne.entity.Priority;
import com.GestionPanne.GestionPanne.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    List<Problem> findByPostId(Long postId);

    List<Problem> findByUserId(Long userId);
}
