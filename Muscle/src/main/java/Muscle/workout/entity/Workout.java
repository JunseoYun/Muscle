package Muscle.workout.entity;
import Muscle.workoutPlan.entity.WorkoutPlan;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "workout")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "writerId")
    private Long writerId;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "workout_plan_id")  // 외래 키 설정
    private WorkoutPlan workoutPlan;

    @Enumerated(EnumType.STRING)
    private WorkoutStatus status = WorkoutStatus.NOT_COMPLETED;

    @Builder
    public Workout(Long writerId, String name, WorkoutPlan workoutPlan) {
        this.writerId = writerId;
        this.name = name;
        this.workoutPlan = workoutPlan;
    }

    public void update(String name) {
        this.name = name;
    }

}
