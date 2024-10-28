package Muscle.proRequest.service;

import Muscle.auth.entity.Auth;
import Muscle.auth.entity.UserRole;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseMessage;
import Muscle.common.service.S3Service;
import Muscle.post.entity.Post;
import Muscle.post.entity.PostImage;
import Muscle.proRequest.dto.RequestPro;
import Muscle.proRequest.dto.ResponsePro;
import Muscle.proRequest.entity.ProCertifyImage;
import Muscle.proRequest.entity.ProRequest;
import Muscle.proRequest.repository.ProCertifyImageRepository;
import Muscle.proRequest.repository.ProRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProRequestService {
    private final AuthRepository authRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final ProRequestRepository proRequestRepository;
    private final ProCertifyImageRepository proCertifyImageRepository;
    private final S3Service s3Service;

    // 프로 신청 보내기
    @Transactional
    public Long send(Optional<String> token, RequestPro.ProRequestDto proRequestDto) {
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

        return proRequest.getId();

    }

    // 인증 자료 사진 업로드
    @Transactional
    public List<String> uploadImg(MultipartFile[] files, long postId) throws IOException {
        ProRequest proRequest = proRequestRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("proRequest not found"));

        List<String> imageUrls = s3Service.uploadFiles(files, "proRequest");

        List<ProCertifyImage> images = imageUrls.stream()
                .map(url -> {
                    ProCertifyImage proCertifyImage = new ProCertifyImage();
                    proCertifyImage.setUrl(url);
                    proCertifyImage.setFileName(url.substring(url.lastIndexOf("/") + 1));
                    proCertifyImage.setProRequest(proRequest);
                    proCertifyImageRepository.save(proCertifyImage);
                    return proCertifyImage;
                })
                .collect(Collectors.toList());

        // 기존의 이미지 리스트를 비운다.
        if (!proRequest.getImages().isEmpty()) {
            proRequest.getImages().clear();  // Hibernate에서 orphan 상태로 관리되기 위해 리스트를 비운다.
        }

        // 새로운 이미지 리스트 설정
        proRequest.getImages().addAll(images);  // 새로운 이미지 리스트를 추가

        proRequestRepository.save(proRequest);  // 변경 사항 저장

        return imageUrls;
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
        for(ProCertifyImage proCertifyImage : proRequest.getImages()) {
            s3Service.deleteFile(proCertifyImage.getUrl());
        }
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

        proRequestRepository.delete(proRequest);


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


    //내 신청 목록 조회
    @Transactional
    public ResponsePro.ProRequesterDto getMyProRequest(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        ProRequest proRequest = proRequestRepository.findByRequester(user);


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



    // 프로 신청 삭제
    @Transactional
    public void deleteProRequest(Optional<String> token, Long proRequestId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("프로 확인 권한 없음");
        }
        ProRequest proRequest = proRequestRepository.findById(proRequestId).get();
        for(ProCertifyImage proCertifyImage : proRequest.getImages()) {
            s3Service.deleteFile(proCertifyImage.getUrl());
        }
        proRequestRepository.delete(proRequest);
    }





}
