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
    public ResponseMessage send(Optional<String> token, RequestPro.ProRequestDto proRequestDto) {
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

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Pro registered successfully.")
                .build();
        return responseMessage;
    }

    //프로 신청 취소
    @Transactional
    public ResponseMessage cancelProRequest(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth requester = authRepository.findByMuscleId(muscleId);
        ProRequest proRequest = proRequestRepository.findByRequester(requester);
        proRequestRepository.delete(proRequest);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest canceled successfully.")
                .build();
        return responseMessage;

    }

    //프로 신청 수락
    @Transactional
    public ResponseMessage acceptProRequest(Optional<String> token, RequestPro.ProAcceptDto proAcceptDto) {
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

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest accepted successfully.")
                .data(requester.getRole())
                .build();
        return responseMessage;
    }



    //프로 신청 거절
    @Transactional
    public ResponseMessage rejectProRequest(Optional<String> token, RequestPro.ProRejectDto proRejectDto) {
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

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest rejected successfully.")
                .data(requester.getRole())
                .build();
        return responseMessage;

    }

    //프로 신청 조회
    @Transactional
    public ResponseMessage getProRequest(Optional<String> token, Long proRequestId) {
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

        ResponsePro.ProRequesterDto response = ResponsePro.ProRequesterDto.toDto(proRequest);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(response)
                .build();
        return responseMessage;


    }

    //프로 신청 목록 조회(전체)
    @Transactional
    public ResponseMessage getAllProRequest(Optional<String> token) {
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

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(dtoList)
                .build();
        return responseMessage;
    }

    //프로 신청 목록 조회(status)
    @Transactional
    public ResponseMessage getStatusProRequest(Optional<String> token, String status) {
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

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(dtoList)
                .build();
        return responseMessage;
    }

    //프로 신청 목록 조회(유저)
    @Transactional
    public ResponseMessage getUserProRequest(Optional<String> token, String requesterMuscleId) {
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

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(response)
                .build();
        return responseMessage;
    }





}
