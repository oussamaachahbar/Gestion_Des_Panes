package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
