package Muscle.workoutPlan.repository;


import Muscle.workoutPlan.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    WorkoutPlan findByWriterIdAndDate(Long writerId, LocalDate date);
}
