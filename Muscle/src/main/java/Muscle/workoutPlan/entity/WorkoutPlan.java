package Muscle.workoutPlan.entity;


import Muscle.auth.repository.AuthRepository;
import Muscle.workout.entity.Workout;
import Muscle.workout.entity.WorkoutStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Table(name = "workoutPlan")
@Entity
@Getter
@NoArgsConstructor
@Data
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "writerId")
    private Long writerId;



    @Column(name = "date")
    private LocalDate date;

    @Column(name = "bodyPart")
    private String bodyPart;

    @Column(name = "completionPercentage")
    private double completionPercentage;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Workout> workoutList;


    @Builder
    public WorkoutPlan(Long writerId, LocalDate date, String bodyPart) {
        this.writerId = writerId;
        this.date = date;
        this.bodyPart = bodyPart;
    }

    public void update(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void addWorkoutList(Workout workout) {
        workoutList.add(workout);
        workout.setWorkoutPlan(this);
    }


    public void completeWorkout(Long workoutId) {
        workoutList.stream()
                .filter(workout -> workout.getId().equals(workoutId))
                .findFirst()
                .ifPresent(workout -> workout.setStatus(WorkoutStatus.COMPLETED));
        checkCompletion();
    }

    public void unCompleteWorkout(Long workoutId) {
        workoutList.stream()
                .filter(workout -> workout.getId().equals(workoutId))
                .findFirst()
                .ifPresent(workout -> workout.setStatus(WorkoutStatus.NOT_COMPLETED));
        checkCompletion();
    }



    private void checkCompletion() {
        long completedWorkouts = workoutList.stream()
                .filter(workout -> workout.getStatus() == WorkoutStatus.COMPLETED)
                .count();
        this.completionPercentage = (double) completedWorkouts / workoutList.size() * 100;
    }






}
