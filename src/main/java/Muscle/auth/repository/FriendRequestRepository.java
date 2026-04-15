package Muscle.auth.repository;

import Muscle.auth.entity.Auth;
import Muscle.auth.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    FriendRequest findByRequester(Auth auth);
    List<FriendRequest> findAllByRequester(Auth auth);

    List<FriendRequest> findAllByRecipientAndStatus(Auth auth, String status);
    FriendRequest findByRequesterAndStatus(Auth auth, String status);
}
