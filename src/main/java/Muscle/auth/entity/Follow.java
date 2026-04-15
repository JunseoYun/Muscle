package Muscle.auth.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Table(name = "follow", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
})
@Entity
@Getter
@NoArgsConstructor
@Data
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Auth follower;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private Auth following;

    //생성자
    public Follow(Auth follower, Auth following) {
        this.follower = follower;
        this.following = following;
    }
}
