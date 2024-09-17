package Muscle.auth.repository;

import Muscle.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByEmail(String email);
    Auth findByMuscleId(String muscleId);
    Auth findByEmailAndPassword(String email, String password);
    Auth findByNaverId(String naverId);

}
