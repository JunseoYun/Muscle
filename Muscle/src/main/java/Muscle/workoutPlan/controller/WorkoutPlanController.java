package Muscle.workoutPlan.controller;

import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseDto;
import Muscle.post.dto.RequestPost;
import Muscle.workoutPlan.dto.RequestWorkoutPlan;
import Muscle.workoutPlan.dto.ResponseWorkoutPlan;
import Muscle.workoutPlan.service.WorkoutPlanService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workoutPlan")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    // 운동 계획 생성
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createWorkoutPlan(HttpServletRequest request, @RequestBody RequestWorkoutPlan.CreateWorkoutPlanDto createWorkoutPlanDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        Long WorkoutPlanId = workoutPlanService.createWorkoutPlan(token, createWorkoutPlanDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("WorkoutPlan created successfully.")
                .data(WorkoutPlanId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    //내 운동 계획 보기
    @GetMapping("/get/{date}")
    public ResponseEntity<ResponseDto> getWorkoutPlan(HttpServletRequest request, @PathVariable("date") LocalDate date) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseWorkoutPlan.GetWorkoutPlanDto response = workoutPlanService.getWorkoutPlan(token, date);
        ResponseDto responseDto = ResponseDto.builder()
                .message("WorkoutPlan retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //이달의 내 운동 계획 보기
    @GetMapping("/monthly")
    public ResponseEntity<ResponseDto> getMonthlyWorkoutPlans(HttpServletRequest request, @RequestParam int year,
                                                              @RequestParam int month) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseWorkoutPlan.GetWorkoutPlanDto> response = workoutPlanService.getMonthlyWorkoutPlans(token, year, month);
        ResponseDto responseDto = ResponseDto.builder()
                .message("WorkoutPlan list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //이달의 친구 운동 계획 보기
    @GetMapping("/monthlyFriend")
    public ResponseEntity<ResponseDto> getMonthlyFriendWorkoutPlans(HttpServletRequest request, @RequestParam int year,
                                                              @RequestParam int month) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseWorkoutPlan.GetWorkoutPlanDto> response = workoutPlanService.getMonthlyFriendWorkoutPlans(token, year, month);
        ResponseDto responseDto = ResponseDto.builder()
                .message("WorkoutPlan list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //친구 운동 계획 보기
    @GetMapping("/getFriend/{date}")
    public ResponseEntity<ResponseDto> getFriendWorkoutPlan(HttpServletRequest request, @PathVariable("date") LocalDate date) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseWorkoutPlan.GetWorkoutPlanDto response = workoutPlanService.getFriendWorkoutPlan(token, date);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Muscle friend workoutPlan retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateWorkoutPlan(HttpServletRequest request, @RequestBody RequestWorkoutPlan.UpdateWorkoutPlanDto updateWorkoutPlanDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        workoutPlanService.updateWorkoutPlan(token, updateWorkoutPlanDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("WorkoutPlan updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{workoutPlanId}")
    public ResponseEntity<ResponseDto> deleteWorkoutPlan(HttpServletRequest request, @PathVariable("workoutPlanId") Long workoutPlanId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        workoutPlanService.deleteWorkoutPlan(token, workoutPlanId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("WorkoutPlan deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
