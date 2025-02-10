package com.GestionPanne.GestionPanne.repo;
import java.util.List;

import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByFamily_Id(Double FamilyId);
    List<Project> findByFiliere_Id(Double UserId);

}
