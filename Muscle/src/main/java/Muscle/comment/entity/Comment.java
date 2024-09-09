package Muscle.comment.entity;


import Muscle.post.entity.Post;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "comment")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    @Column(name = "commentWriterId")
    private Long commentWriterId;

    @Column(name = "commentContent")
    private String commentContent;

    @Column(name = "commentDate")
    private LocalDateTime commentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public Comment(Long commentWriterId, String commentContent, LocalDateTime commentDate, Post post) {
        this.commentWriterId = commentWriterId;
        this.commentContent = commentContent;
        this.commentDate = commentDate;
        this.post = post;
    }

    public void update(String commentContent, LocalDateTime commentDate) {
        this.commentContent = commentContent;
        this.commentDate = commentDate;
    }
}
