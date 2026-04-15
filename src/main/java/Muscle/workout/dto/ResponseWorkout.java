package Muscle.workout.dto;

import Muscle.workout.entity.Workout;
import Muscle.workout.entity.WorkoutStatus;
import lombok.Builder;
import lombok.Getter;

public class ResponseWorkout {

    @Builder
    @Getter
    public static class GetWorkoutDto {
        private Long workoutWriterId;
        private Long workoutId;
        private String workoutName;
        private WorkoutStatus workoutStatus;

        public static GetWorkoutDto toDto(Workout workout) {
            return GetWorkoutDto.builder()
                    .workoutWriterId(workout.getWriterId())
                    .workoutId(workout.getId())
                    .workoutName(workout.getName())
                    .workoutStatus(workout.getStatus())
                    .build();
        }
    }
}
