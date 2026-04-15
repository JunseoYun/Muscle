package Muscle.workoutPlan.dto;


import Muscle.workoutPlan.entity.WorkoutPlan;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

public class RequestWorkoutPlan {

    @Data
    @Builder
    public static class CreateWorkoutPlanDto {
        private LocalDate date;
        private String bodyPart;

        public static WorkoutPlan toEntity(CreateWorkoutPlanDto createWorkoutPlanDto, Long writerId) {
            return WorkoutPlan.builder()
                    .writerId(writerId)
                    .date(createWorkoutPlanDto.getDate())
                    .bodyPart(createWorkoutPlanDto.getBodyPart())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateWorkoutPlanDto {
        private Long workoutPlanId;
        private String bodyPart;


    }
}
