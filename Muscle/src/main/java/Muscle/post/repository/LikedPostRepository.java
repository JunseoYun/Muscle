package Muscle.post.repository;

import Muscle.post.entity.LikedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    LikedPost findByUserIdAndPostId(Long userId, Long postId);
    List<LikedPost> findAllByUserId(Long userId);
    List<LikedPost> findAllByPostId(Long postId);
}
