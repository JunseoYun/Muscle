package Muscle.post.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Table(name = "LikedPost")
@Entity
@Getter
@NoArgsConstructor
@Data
public class LikedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long likedPostId;

    @Column(name = "storeId")
    private Long postId;

    @Column(name = "userId")
    private Long userId;

    @Builder
    public LikedPost(Long postId, Long userId){
        this.postId = postId;
        this.userId = userId;
    }
}
