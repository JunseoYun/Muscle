package Muscle.workout.dto;

import Muscle.workout.entity.Workout;
import Muscle.workoutPlan.entity.WorkoutPlan;
import lombok.Builder;
import lombok.Data;

public class RequestWorkout {

    @Data
    @Builder
    public static class CreateWorkoutDto {
        private String workoutName;
        private Long workoutPlanId;

        public static Workout toEntity(CreateWorkoutDto createWorkoutDto, Long workoutWriterId, WorkoutPlan workoutPlan) {
            return Workout.builder()
                    .writerId(workoutWriterId)
                    .name(createWorkoutDto.getWorkoutName())
                    .workoutPlan(workoutPlan)
                    .build();
        }

    }


    @Data
    @Builder
    public static class UpdateWorkoutDto {
        private Long workoutId;
        private String workoutName;
    }

    @Data
    @Builder
    public static class CompleteWorkoutDto {
        private Long workoutId;
        private String temp;
    }
}
