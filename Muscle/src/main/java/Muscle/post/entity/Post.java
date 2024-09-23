package Muscle.post.entity;

import Muscle.comment.entity.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "post")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long postId;

    @Column(name = "writerId")
    private Long writerId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "board")
    private String board;

    @Column(name = "postLikeCount")
    private Long postLikeCount = 0L;

    @Column(name = "postCommentCount")
    private Long postCommentCount = 0L;

    @Column(name = "postReportCount")
    private Long postReportCount = 0L;

    @Column(name = "postDate")
    private LocalDateTime postDate;

    @Column(name = "postImg")
    private String postImg;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();


    @Builder
    public Post(Long writerId, String title, String content, String board) {
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.board = board;
        this.postDate = LocalDateTime.now();
    }

    public  void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.postDate = LocalDateTime.now();
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
    }


    public void increasePostLikeCount(){ this. postLikeCount++; }
    public void decreasePostLikeCount(){ this. postLikeCount--; }
    public void increasePostCommentCount(){ this. postCommentCount++; }
    public void decreasePostCommentCount(){ this. postCommentCount--; }
}
