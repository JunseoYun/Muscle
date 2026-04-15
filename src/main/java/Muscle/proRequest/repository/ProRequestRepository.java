package Muscle.proRequest.repository;


import Muscle.auth.entity.Auth;
import Muscle.proRequest.entity.ProRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProRequestRepository extends JpaRepository<ProRequest, Long> {
    List<ProRequest> findAllByStatus(String status);
    ProRequest findByRequester(Auth user);
}
