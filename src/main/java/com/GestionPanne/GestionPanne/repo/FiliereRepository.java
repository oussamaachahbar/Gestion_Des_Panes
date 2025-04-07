package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    boolean existsByName(String name);
    Optional<Filiere> findByName(String name);

}
