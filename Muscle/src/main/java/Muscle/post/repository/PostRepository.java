package Muscle.post.repository;


import Muscle.post.entity.Post;
import Muscle.post.entity.PostRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByWriterId(Long writerId);
    List<Post> findByBoard(String board);
    @Query("SELECT a FROM Post a WHERE a.title = :title " +
            "OR a.title LIKE %:title% ORDER BY CASE WHEN a.title = :title THEN 0 ELSE 1 END, a.title ASC")
    List<Post> findByTitleContainingOrdered(@PathVariable("title") String title);
    List<Post> findByReportCountGreaterThanEqual(Long reportCount);

    //베스트 게시글 조회(전체 게시글 중 좋아요 많은 순 10걔) - 비로그인
    List<Post> findTop10ByOrderByLikeCountDesc();


    //투데이 베스트 게시글 조회(24시간이 지나지 않은 게시글 중 좋아요 많은 순 10개) - 비로그인
    List<Post> findTop10ByPostDateAfterOrderByLikeCountDesc(LocalDateTime localDateTime);

    //각 게시판 최근 게시글 10개 - 비로그인
    List<Post> findTop10ByPostRoleOrderByPostDateDesc(PostRole postRole);


    //세부 게시판 베스트 게시글 조회(좋아요 많은 순 10개) - 로그인
    List<Post> findTop10ByBoardOrderByLikeCountDesc(String board);

    //세부 게시판 게시글 최신 순 조회 - 로그인
    List<Post> findByBoardOrderByPostDateDesc(String board);


    //특정 유저 게시글 조회
    List<Post> findAllByWriterIdOrderByPostDateDesc(Long writerId);


    //내가 팔로우한 사람들의 최신 게시글 순 조회 - 로그인
    List<Post> findAllByWriterIdInOrderByPostDateDesc(List<Long> writerIds);







}
