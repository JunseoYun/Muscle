package Muscle.post.repository;


import Muscle.post.entity.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    SavedPost findByUserIdAndPostId(Long userId, Long storeId);
    List<SavedPost> findAllByUserId(Long userId);
}
