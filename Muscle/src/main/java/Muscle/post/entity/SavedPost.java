package Muscle.post.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Table(name = "SavedPost")
@Entity
@Getter
@NoArgsConstructor
@Data
public class SavedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long savedPostId;

    @Column(name = "storeId")
    private Long postId;

    @Column(name = "userId")
    private Long userId;

    @Builder
    public SavedPost(Long postId, Long userId){
        this.postId = postId;
        this.userId = userId;
    }
}
