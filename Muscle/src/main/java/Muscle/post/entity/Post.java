package Muscle.post.entity;

import Muscle.comment.entity.Comment;
import Muscle.postReport.entity.PostReport;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "postRole")
    private PostRole postRole;

    @Column(name = "board")
    private String board;

    @Column(name = "likeCount")
    private Long likeCount = 0L;

    @Column(name = "commentCount")
    private Long commentCount = 0L;

    @Column(name = "reportCount")
    private Long reportCount = 0L;

    @Column(name = "postDate")
    private LocalDateTime postDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostReport> reportList = new ArrayList<>();

    @Builder
    public Post(Long writerId, String title, String content, String board, PostRole postRole) {
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.postRole = postRole;
        this.board = board;
        this.postDate = LocalDateTime.now();
    }

    public  void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
        this.commentCount++;
    }
    public void addReport(PostReport postReport) {
        this.reportList.add(postReport);
        this.reportCount++;
    }


}
