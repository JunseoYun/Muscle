package Muscle.proRequest.controller;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseMessage;
import Muscle.proRequest.dto.RequestPro;
import Muscle.proRequest.service.ProRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/proRequest")
@RequiredArgsConstructor
public class ProRequestController {

    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final ProRequestService proRequestService;

    //프로 신청 보내기
    @PostMapping("/send")
    public ResponseEntity<ResponseMessage> send(HttpServletRequest request, @Valid @RequestBody RequestPro.ProRequestDto proRequestDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.send(token, proRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 취소
    @DeleteMapping("cancelProRequest")
    public ResponseEntity<ResponseMessage> cancelProRequest (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.cancelProRequest(token);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 수락
    @PostMapping("acceptProRequest")
    public ResponseEntity<ResponseMessage> acceptProRequest (HttpServletRequest request, @Valid @RequestBody RequestPro.ProAcceptDto proAcceptDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.acceptProRequest(token, proAcceptDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 거절
    @PostMapping("rejectProRequest")
    public ResponseEntity<ResponseMessage> rejectProRequest (HttpServletRequest request, @Valid @RequestBody RequestPro.ProRejectDto proRejectDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.rejectProRequest(token, proRejectDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 조회
    @GetMapping("getProRequest")
    public ResponseEntity<ResponseMessage> getProRequest (HttpServletRequest request, @RequestParam Long proRequestId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.getProRequest(token, proRequestId);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 목록 조회(전체)
    @GetMapping("getAllProRequest")
    public ResponseEntity<ResponseMessage> getAllProRequest (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.getAllProRequest(token);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 목록 조회(status)
    @GetMapping("getStatusProRequest")
    public ResponseEntity<ResponseMessage> getStatusProRequest (HttpServletRequest request, @RequestParam String status) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.getStatusProRequest(token, status);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 목록 조회(전체)
    @GetMapping("getUserProRequest")
    public ResponseEntity<ResponseMessage> getUserProRequest (HttpServletRequest request, @RequestParam String requesterMuscleId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseMessage responseMessage = proRequestService.getUserProRequest(token, requesterMuscleId);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }





}
