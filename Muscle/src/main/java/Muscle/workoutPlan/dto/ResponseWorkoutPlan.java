package Muscle.workoutPlan.dto;

import Muscle.auth.repository.AuthRepository;
import Muscle.workout.entity.Workout;
import Muscle.workout.entity.WorkoutStatus;
import Muscle.workoutPlan.entity.WorkoutPlan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResponseWorkoutPlan {



    @Getter
    @Builder
    public static class GetWorkoutPlanDto {
        private Long writerId;
        private String muscleId;
        private Long workoutPlanId;
        private LocalDate date;
        private String bodyPart;
        private boolean isComplete;
        private List<WorkoutListDto> workoutList;

        public static GetWorkoutPlanDto toDto(WorkoutPlan workoutPlan) {
            List<WorkoutListDto> workoutList = new ArrayList<>();
            if(!workoutPlan.getWorkoutList().isEmpty()) {
                workoutPlan.getWorkoutList().stream().forEach(workout -> workoutList.add(WorkoutListDto.toDto(workout)));
            }

            return GetWorkoutPlanDto.builder()
                    .writerId(workoutPlan.getWriterId())
                    .workoutPlanId(workoutPlan.getId())
                    .date(workoutPlan.getDate())
                    .bodyPart(workoutPlan.getBodyPart())
                    .isComplete(workoutPlan.isComplete())
                    .workoutList(workoutList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class WorkoutListDto {
        private Long workoutId;
        private Long workoutWriterId;
        private String workoutName;
        private WorkoutStatus workoutStatus;

        public static WorkoutListDto toDto(Workout workout) {
            return WorkoutListDto.builder()
                    .workoutId(workout.getId())
                    .workoutWriterId(workout.getWriterId())
                    .workoutName(workout.getName())
                    .workoutStatus(workout.getStatus())
                    .build();
        }
    }
}
