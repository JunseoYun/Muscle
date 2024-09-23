package Muscle.post.repository;


import Muscle.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByWriterId(Long writerId);
    List<Post> findByBoard(String board);
    @Query("SELECT a FROM Post a WHERE a.title = :title " +
            "OR a.title LIKE %:title% ORDER BY CASE WHEN a.title = :title THEN 0 ELSE 1 END, a.title ASC")
    List<Post> findByTitleContainingOrdered(@PathVariable("title") String title);

}
