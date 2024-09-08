package Muscle.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

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
    private LocalTime postDate;


    @Builder
    public Post(Long writerId, String title, String content, String board, LocalTime postDate) {
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.board = board;
        this.postDate = postDate;
    }

    public  void update(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public void increasePostLikeCount(){ this. postLikeCount++; }
    public void decreasePostLikeCount(){ this. postLikeCount--; }
    public void increasePostCommentCount(){ this. postCommentCount++; }
    public void decreasePostCommentCount(){ this. postCommentCount--; }
}
