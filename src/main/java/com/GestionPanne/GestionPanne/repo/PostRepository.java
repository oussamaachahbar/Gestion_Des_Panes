package com.GestionPanne.GestionPanne.repo;
import java.util.List;
import com.GestionPanne.GestionPanne.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PostRepository extends JpaRepository<Post, Integer> {

List<Post> findByProject_Id(Double projectId);
List<Post> findByUser_Id(Double UserId);

}
