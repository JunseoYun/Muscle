package Muscle.postReport.repository;

import Muscle.post.entity.Post;
import Muscle.postReport.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findAllByPost(Post post);
    PostReport findByPostAndReporterId(Post post, Long reporterId);
    List<PostReport> findAllByReporterId(Long reporterId);
}
