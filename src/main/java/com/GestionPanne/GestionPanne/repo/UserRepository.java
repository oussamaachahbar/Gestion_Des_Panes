package com.GestionPanne.GestionPanne.repo;

import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByPost(Post post);
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.post.id = :postId AND u.isSupervisor = true")
    boolean existsByPostIdAndIsSupervisorTrue(@Param("postId") Long postId);
    @Query("SELECT u FROM User u WHERE u.role = 'USER'")
    List<User> findByUserRole();

    Optional<Object> findByName(String currentUserName);
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE users MODIFY COLUMN post_id BIGINT NULL", nativeQuery = true)
    void makePostIdNullable();
}
