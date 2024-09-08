package Muscle.post.repository;


import Muscle.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByWriterId(Long writerId);
    List<Post> findByBoard(String board);
}
