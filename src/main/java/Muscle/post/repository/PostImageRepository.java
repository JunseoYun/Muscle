package Muscle.post.repository;

import Muscle.post.entity.Post;
import Muscle.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
