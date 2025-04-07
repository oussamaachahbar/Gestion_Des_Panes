package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Project;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByNameProject(String nameProject);

    List<Project> findByFamilyId(Long familyId);

    List<Project> findByFiliereId(long id);
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE projects MODIFY COLUMN family_id BIGINT NULL", nativeQuery = true)
    void makeFamilyIdNullable();
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE projects MODIFY COLUMN filiere_id BIGINT NULL", nativeQuery = true)
    void makeFiliereIdNullable();


    Project findByNameProject(String nameProject);
}
