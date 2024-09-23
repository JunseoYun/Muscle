package Muscle.workout.repository;

import Muscle.workout.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
//    List<Workout> findByAll();
}
