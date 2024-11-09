package Muscle.workout.service;

import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.workout.dto.RequestWorkout;
import Muscle.workout.dto.ResponseWorkout;
import Muscle.workout.entity.Workout;
import Muscle.workout.entity.WorkoutStatus;
import Muscle.workout.repository.WorkoutRepository;
import Muscle.workoutPlan.entity.WorkoutPlan;
import Muscle.workoutPlan.repository.WorkoutPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    //운동 생성
    public Long createWorkout(Optional<String> token, RequestWorkout.CreateWorkoutDto createWorkoutDto) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth writer = authRepository.findByMuscleId(muscleId);
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(createWorkoutDto.getWorkoutPlanId()).get();
        if(workoutPlan == null) {
            throw new EntityNotFoundException();
        }
        if(!Objects.equals(workoutPlan.getWriterId(), writer.getId()) && !Objects.equals(workoutPlan.getWriterId(), writer.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        Workout workout = RequestWorkout.CreateWorkoutDto.toEntity(createWorkoutDto, writer.getId(), workoutPlan);
        workoutRepository.save(workout);
        workoutPlan.addWorkoutList(workout);
        return workout.getId();

    }

    //운동 목록 전체 조회
    public List<ResponseWorkout.GetWorkoutDto> getAllWorkout() {
        List<Workout> entityList = workoutRepository.findAll();
        List<ResponseWorkout.GetWorkoutDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(workout -> dtoList.add(ResponseWorkout.GetWorkoutDto.toDto(workout)));
        return dtoList;
    }

    //운동 조회
    public ResponseWorkout.GetWorkoutDto getWorkout(Long id) {
        Workout workout = workoutRepository.findById(id).get();
        return ResponseWorkout.GetWorkoutDto.toDto(workout);
    }

    //운동 목록 조회
    public List<ResponseWorkout.GetWorkoutDto> getPlanWorkout(Optional<String> token, Long workoutPlanId) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId).get();
        Auth planer = authRepository.findById(workoutPlan.getWriterId()).get();
        if(user != planer && user != planer.getMuscleFriend()) {
            throw new IllegalArgumentException("권한 없음");
        }
        List<Workout> entityList = workoutRepository.findAllByWorkoutPlan(workoutPlan);
        List<ResponseWorkout.GetWorkoutDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(workout -> {
            dtoList.add(ResponseWorkout.GetWorkoutDto.toDto(workout));
        });
        return dtoList;
    }

    //운동 완료
    public WorkoutStatus completeWorkout(Optional<String> token, RequestWorkout.CompleteWorkoutDto completeWorkoutDto) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        Workout workout = workoutRepository.findById(completeWorkoutDto.getWorkoutId()).get();
        WorkoutPlan workoutPlan = workout.getWorkoutPlan();
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        workout.setStatus(WorkoutStatus.COMPLETED);
        workoutRepository.save(workout);
        return workout.getStatus();
    }

    //운동 완료 취소
    public WorkoutStatus unCompleteWorkout(Optional<String> token, RequestWorkout.CompleteWorkoutDto completeWorkoutDto) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        Workout workout = workoutRepository.findById(completeWorkoutDto.getWorkoutId()).get();
        WorkoutPlan workoutPlan = workout.getWorkoutPlan();
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        workout.setStatus(WorkoutStatus.NOT_COMPLETED);
        workoutRepository.save(workout);
        return workout.getStatus();
    }

    //운동 수정
    public void updateWorkout(Optional<String> token, RequestWorkout.UpdateWorkoutDto updateWorkoutDto) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        Workout workout = workoutRepository.findById(updateWorkoutDto.getWorkoutId()).get();
        WorkoutPlan workoutPlan = workout.getWorkoutPlan();
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        workout.update(updateWorkoutDto.getWorkoutName());
        workoutRepository.save(workout);
    }

    //운동 삭제
    public void deleteWorkout(Optional<String> token, Long workoutId) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        Workout workout = workoutRepository.findById(workoutId).get();
        WorkoutPlan workoutPlan = workout.getWorkoutPlan();
        if(!Objects.equals(workoutPlan.getWriterId(), user.getId()) && !Objects.equals(workoutPlan.getWriterId(), user.getMuscleFriend().getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
        workoutRepository.delete(workout);
    }
}
