package Muscle.workoutPlan.repository;


import Muscle.workoutPlan.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    WorkoutPlan findByWriterIdAndDate(Long writerId, LocalDate date);
    boolean existsByWriterIdAndDate(Long writerId, LocalDate date);
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.writerId = :writerId AND YEAR(wp.date) = :year AND MONTH(wp.date) = :month")
    List<WorkoutPlan> findAllByWriterIdAndMonth(@Param("writerId") Long writerId, @Param("year") int year, @Param("month") int month);
}
