package Muscle.workoutPlan.service;

import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.workoutPlan.dto.RequestWorkoutPlan;
import Muscle.workoutPlan.dto.ResponseWorkoutPlan;
import Muscle.workoutPlan.entity.WorkoutPlan;
import Muscle.workoutPlan.repository.WorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {
    private final WorkoutPlanRepository workoutPlanRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;


    // 운동 계획 생성
    public Long createWorkoutPlan(Optional<String> token, RequestWorkoutPlan.CreateWorkoutPlanDto createWorkoutPlanDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByMuscleId(muscleId).getId();
        boolean isWorkoutPlan = workoutPlanRepository.existsByWriterIdAndDate(writerId, createWorkoutPlanDto.getDate());
        if(isWorkoutPlan) {
            throw new IllegalArgumentException("해당 날짜에 이미 운동 플랜 존재");
        }
        WorkoutPlan workoutPlan = RequestWorkoutPlan.CreateWorkoutPlanDto.toEntity(createWorkoutPlanDto, writerId);
        workoutPlanRepository.save(workoutPlan);
        return workoutPlan.getId();
    }



    //내 운동 계획 보기
    public ResponseWorkoutPlan.GetWorkoutPlanDto getWorkoutPlan(Optional<String> token, LocalDate date) {

        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);

        WorkoutPlan workoutPlan = workoutPlanRepository.findByWriterIdAndDate(user.getId(), date);
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        return ResponseWorkoutPlan.GetWorkoutPlanDto.toDto(user, workoutPlan);
    }

    // 이달의 운동 계획 보기
    public List<ResponseWorkoutPlan.GetWorkoutPlanDto> getMonthlyWorkoutPlans(Optional<String> token, int year, int month) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);

        // 이달의 운동 계획 조회
        List<WorkoutPlan> workoutPlans = workoutPlanRepository.findAllByWriterIdAndMonth(user.getId(), year, month);

        // DTO 리스트로 변환
        List<ResponseWorkoutPlan.GetWorkoutPlanDto> dtoList = new ArrayList<>();
        for (WorkoutPlan workoutPlan : workoutPlans) {
            dtoList.add(ResponseWorkoutPlan.GetWorkoutPlanDto.toDto(user, workoutPlan));
        }

        return dtoList;
    }


    //친구 운동 계획 보기
    public ResponseWorkoutPlan.GetWorkoutPlanDto getFriendWorkoutPlan(Optional<String> token, LocalDate date) {

        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);

        WorkoutPlan workoutPlan = workoutPlanRepository.findByWriterIdAndDate(user.getMuscleFriend().getId(), date);
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        return ResponseWorkoutPlan.GetWorkoutPlanDto.toDto(user.getMuscleFriend(), workoutPlan);
    }


    //운동 계획 업데이트
    public void updateWorkoutPlan(Optional<String> token, RequestWorkoutPlan.UpdateWorkoutPlanDto updateWorkoutPlanDto) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        WorkoutPlan workoutPlan = workoutPlanRepository.findById((updateWorkoutPlanDto.getWorkoutPlanId())).get();
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        workoutPlan.update(updateWorkoutPlanDto.getBodyPart());
        workoutPlanRepository.save(workoutPlan);

    }

    //운동 게획 삭제
    public void deleteWorkoutPlan(Optional<String> token, Long workoutPlanId) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByMuscleId(muscleId);
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId).get();
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        workoutPlanRepository.delete(workoutPlan);
    }

}
