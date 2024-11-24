package Muscle.auth.repository;

import Muscle.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByEmail(String email);
    Auth findByMuscleId(String muscleId);
    Auth findByEmailAndPassword(String email, String password);
    Auth findByMuscleIdAndPassword(String muscleId, String password);
    Auth findByNameAndEmail(String name, String email);
    Auth findByMuscleIdAndNameAndEmail(String muscleId, String name, String email);
    Auth findByNaverId(String naverId);
    Auth findByKakaoId(String kakaoId);
    @Query("SELECT a FROM Auth a WHERE a.muscleId = :muscleId " +
            "OR a.muscleId LIKE %:muscleId% ORDER BY CASE WHEN a.muscleId = :muscleId THEN 0 ELSE 1 END, a.muscleId ASC")
    List<Auth> findByMuscleIdContainingOrdered(@PathVariable("muscleId") String muscleId);

    @Query(value = """
        SELECT a FROM Auth a
        WHERE (
            6371 * acos(
                cos(radians(:latitude)) * cos(radians(a.latitude)) *
                cos(radians(a.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(a.latitude))
            )
        ) <= 10
        AND a.level = :level
    """)
    List<Auth> findNearbyUsersByLevel(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("level") String level
    );


}
