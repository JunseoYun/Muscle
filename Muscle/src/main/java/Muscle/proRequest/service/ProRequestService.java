package Muscle.proRequest.service;

import Muscle.auth.entity.Auth;
import Muscle.auth.entity.UserRole;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseMessage;
import Muscle.post.entity.Post;
import Muscle.proRequest.dto.RequestPro;
import Muscle.proRequest.dto.ResponsePro;
import Muscle.proRequest.entity.ProRequest;
import Muscle.proRequest.repository.ProRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProRequestService {
    private final AuthRepository authRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final ProRequestRepository proRequestRepository;

    // 프로 신청 보내기
    @Transactional
    public void send(Optional<String> token, RequestPro.ProRequestDto proRequestDto) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth requester = authRepository.findByMuscleId(muscleId);

        if(proRequestRepository.findByRequester(requester) != null) {
            throw new IllegalArgumentException("이미 보낸 프로 신청이 있습니다.");
        }
        ProRequest proRequest = RequestPro.ProRequestDto.toEntity(proRequestDto, requester);
        proRequestRepository.save(proRequest);

    }

    //프로 신청 취소
    @Transactional
    public void cancelProRequest(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth requester = authRepository.findByMuscleId(muscleId);
        ProRequest proRequest = proRequestRepository.findByRequester(requester);
        proRequestRepository.delete(proRequest);

    }

    //프로 신청 수락
    @Transactional
    public void acceptProRequest(Optional<String> token, RequestPro.ProAcceptDto proAcceptDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("프로 수락 권한 없음");
        }
        Auth requester = authRepository.findById(proAcceptDto.getUserId()).get();
        ProRequest proRequest = proRequestRepository.findByRequester(requester);
        proRequest.setStatus("ACCEPTED");

        requester.setRole(UserRole.PRO);
        requester.setLevel(proRequest.getProField());
        authRepository.save(requester);


    }



    //프로 신청 거절
    @Transactional
    public void rejectProRequest(Optional<String> token, RequestPro.ProRejectDto proRejectDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("프로 거절 권한 없음");
        }
        Auth requester = authRepository.findById(proRejectDto.getUserId()).get();
        ProRequest proRequest = proRequestRepository.findByRequester(requester);

        proRequest.setStatus("REJECTED");
        proRequestRepository.save(proRequest);


    }

    //프로 신청 조회
    @Transactional
    public ResponsePro.ProRequesterDto getProRequest(Optional<String> token, Long proRequestId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("프로 확인 권한 없음");
        }
        ProRequest proRequest = proRequestRepository.findById(proRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + proRequestId));


        return  ResponsePro.ProRequesterDto.toDto(proRequest);


    }

    //프로 신청 목록 조회(전체)
    @Transactional
    public List<ResponsePro.ProRequestListDto> getAllProRequest(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("프로 확인 권한 없음");
        }
        List<ProRequest> entityList = proRequestRepository.findAll();
        List<ResponsePro.ProRequestListDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(proRequest -> dtoList.add(ResponsePro.ProRequestListDto.toDto(proRequest)));


        return dtoList;
    }

    //프로 신청 목록 조회(status)
    @Transactional
    public List<ResponsePro.ProRequestListDto> getStatusProRequest(Optional<String> token, String status) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("프로 확인 권한 없음");
        }
        List<ProRequest> entityList = proRequestRepository.findAllByStatus(status);
        List<ResponsePro.ProRequestListDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(proRequest -> dtoList.add(ResponsePro.ProRequestListDto.toDto(proRequest)));


        return dtoList;
    }

    //프로 신청 목록 조회(유저)
    @Transactional
    public ResponsePro.ProRequestListDto getUserProRequest(Optional<String> token, String requesterMuscleId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
//        if(admin.getRole() != UserRole.ADMIN) {
//            throw new IllegalArgumentException("프로 확인 권한 없음");
//        }
        Auth requester = authRepository.findByMuscleId(requesterMuscleId);

        ProRequest proRequest = proRequestRepository.findByRequester(requester);

        ResponsePro.ProRequestListDto response = ResponsePro.ProRequestListDto.toDto(proRequest);


        return response;
    }





}
