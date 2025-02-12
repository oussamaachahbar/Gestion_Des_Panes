package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FiliereRepository extends JpaRepository<Filiere, Long> { // Correction du type ID
}
