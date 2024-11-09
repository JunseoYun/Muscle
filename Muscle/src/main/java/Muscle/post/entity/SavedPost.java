package Muscle.post.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "SavedPost")
@Entity
@Getter
@NoArgsConstructor
@Data
public class SavedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long savedPostId;

    @Column(name = "postId")
    private Long postId;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Builder
    public SavedPost(Long postId, Long userId){
        this.postId = postId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
