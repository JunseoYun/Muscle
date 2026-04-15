package Muscle.auth.repository;

import Muscle.auth.entity.Auth;
import Muscle.auth.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(Auth auth);
    List<Follow> findByFollowing(Auth auth);

    Follow findByFollowerAndFollowing(Auth follower, Auth following);


    boolean existsByFollowerAndFollowing(Auth user, Auth writer);
}
