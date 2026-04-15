package Muscle.workout.controller;

import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseDto;
import Muscle.workout.dto.RequestWorkout;
import Muscle.workout.dto.ResponseWorkout;
import Muscle.workout.entity.WorkoutStatus;
import Muscle.workout.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout")
public class WorkoutController {
    @Autowired
    private final WorkoutService workoutService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;


    //운동 생성
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createWorkout(HttpServletRequest request, @RequestBody RequestWorkout.CreateWorkoutDto createWorkoutDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseWorkout.GetWorkoutDto response = workoutService.createWorkout(token, createWorkoutDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout created successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    //운동 목록 전체 조회
    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllWorkout() {
        List<ResponseWorkout.GetWorkoutDto> response = workoutService.getAllWorkout();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    //운동 조회
    @GetMapping("/get/{workoutId}")
    public ResponseEntity<ResponseDto> getWorkout(@PathVariable("workoutId") Long workoutId) {
        ResponseWorkout.GetWorkoutDto response = workoutService.getWorkout(workoutId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //운동 조회
    @GetMapping("/getPlanWorkout/{workoutPlanId}")
    public ResponseEntity<ResponseDto> getPlanWorkout(HttpServletRequest request, @PathVariable("workoutPlanId") Long workoutPlanId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }

        List<ResponseWorkout.GetWorkoutDto> response = workoutService.getPlanWorkout(token, workoutPlanId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }



    //운동 완료
    @PostMapping("/complete")
    public ResponseEntity<ResponseDto> completeWorkout(HttpServletRequest request, @RequestBody RequestWorkout.CompleteWorkoutDto completeWorkoutDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        WorkoutStatus workoutStatus = workoutService.completeWorkout(token, completeWorkoutDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout completed successfully.")
                .data(workoutStatus)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //운동 완료 취소
    @PostMapping("/unComplete")
    public ResponseEntity<ResponseDto> unCompleteWorkout(HttpServletRequest request, @RequestBody RequestWorkout.CompleteWorkoutDto completeWorkoutDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        WorkoutStatus workoutStatus = workoutService.unCompleteWorkout(token, completeWorkoutDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout uncompleted successfully.")
                .data(workoutStatus)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //운동 수정
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateWorkout(HttpServletRequest request, @RequestBody RequestWorkout.UpdateWorkoutDto updateWorkoutDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        workoutService.updateWorkout(token, updateWorkoutDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    //운동 삭제
    @DeleteMapping("/delete/{workoutId}")
    public ResponseEntity<ResponseDto> deleteWorkout(HttpServletRequest request, @PathVariable("workoutId") Long workoutId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        workoutService.deleteWorkout(token, workoutId);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Workout deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
