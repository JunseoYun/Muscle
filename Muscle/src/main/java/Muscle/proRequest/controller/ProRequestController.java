package Muscle.proRequest.controller;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseMessage;
import Muscle.proRequest.dto.RequestPro;
import Muscle.proRequest.dto.ResponsePro;
import Muscle.proRequest.service.ProRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
        proRequestService.send(token, proRequestDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Pro registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 취소
    @DeleteMapping("cancelProRequest")
    public ResponseEntity<ResponseMessage> cancelProRequest (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        proRequestService.cancelProRequest(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest canceled successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 수락
    @PostMapping("acceptProRequest")
    public ResponseEntity<ResponseMessage> acceptProRequest (HttpServletRequest request, @Valid @RequestBody RequestPro.ProAcceptDto proAcceptDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        proRequestService.acceptProRequest(token, proAcceptDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest accepted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 거절
    @PostMapping("rejectProRequest")
    public ResponseEntity<ResponseMessage> rejectProRequest (HttpServletRequest request, @Valid @RequestBody RequestPro.ProRejectDto proRejectDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        proRequestService.rejectProRequest(token, proRejectDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest rejected successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 조회
    @GetMapping("getProRequest")
    public ResponseEntity<ResponseMessage> getProRequest (HttpServletRequest request, @RequestParam Long proRequestId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponsePro.ProRequesterDto response = proRequestService.getProRequest(token, proRequestId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 목록 조회(전체)
    @GetMapping("getAllProRequest")
    public ResponseEntity<ResponseMessage> getAllProRequest (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePro.ProRequestListDto> response = proRequestService.getAllProRequest(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 목록 조회(status)
    @GetMapping("getStatusProRequest/{status}")
    public ResponseEntity<ResponseMessage> getStatusProRequest (HttpServletRequest request, @PathVariable("status") String status) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePro.ProRequestListDto> dtoList = proRequestService.getStatusProRequest(token, status);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //프로 신청 목록 조회(유저)
    @GetMapping("getUserProRequest/{muscleId}")
    public ResponseEntity<ResponseMessage> getUserProRequest (HttpServletRequest request, @PathVariable("muscleId") String muscleId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponsePro.ProRequestListDto response = proRequestService.getUserProRequest(token, muscleId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("ProRequest retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }





}
