package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FamilyRepository extends JpaRepository<Family, Long> {

    boolean existsByName(String name);
}
