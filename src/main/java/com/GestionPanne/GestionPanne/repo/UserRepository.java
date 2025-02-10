package com.GestionPanne.GestionPanne.repo;
import java.util.List;
import com.GestionPanne.GestionPanne.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
