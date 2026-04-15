package Muscle.postReport.entity;

import Muscle.post.entity.Post;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "postReport")
@Entity
@Getter
@NoArgsConstructor
@Data
public class PostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reportId;

    @Column(name = "reportWriterId")
    private Long reporterId;

    @Column(name = "reportReason")
    private String reportReason;

    @Column(name = "reportDate")
    private LocalDateTime reportDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostReport(Long reporterId, String reportReason, Post post) {
        this.reporterId = reporterId;
        this.reportReason = reportReason;
        this.reportDate = LocalDateTime.now();
        this.post = post;
    }

}
