package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Etat;
import com.GestionPanne.GestionPanne.entity.Priority;
import com.GestionPanne.GestionPanne.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    List<Problem> findByPostId(Long postId);

    List<Problem> findByUserId(Long userId);
    @Query("SELECT p FROM Problem p ORDER BY CASE WHEN p.priority = 'CRITICAL' THEN 1 WHEN p.priority = 'HIGH' THEN 2 WHEN p.priority = 'MEDIUM' THEN 3 WHEN p.priority = 'LOW' THEN 4 END ASC")
    List<Problem> findAllOrderedByPriority();
}
