package com.GestionPanne.GestionPanne.repo;
import java.util.List;

import com.GestionPanne.GestionPanne.entity.Etat;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.Priority;
import com.GestionPanne.GestionPanne.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProblemRepository extends JpaRepository<Problem, Integer>{

    List<Problem> findByUser_Id(Double UserId);
    List<Problem> findByPost_Id(Double PostId);
    List<Problem> findByProject_Id(Double ProjectId);
    List<Problem> findByPriority(Priority priority);
    List<Problem> findByEtat(Etat etat);


}
