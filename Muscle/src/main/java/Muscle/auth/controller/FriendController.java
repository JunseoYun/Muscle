package Muscle.auth.controller;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.auth.service.EmailService;
import Muscle.auth.service.FriendService;
import Muscle.common.dto.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {


    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final FriendService friendService;
    private final EmailService emailService;


    //친구 요청 보내기
    @PostMapping("/request")
    public ResponseEntity<ResponseMessage> sendFriendRequest(HttpServletRequest request, @Valid @RequestBody RequestAuth.FriendRequestDto friendRequestDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }

        friendService.sendFriendRequest(token, friendRequestDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Muscle friend request successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }


    //친구 요청 취소
    @DeleteMapping("/cancel")
    public ResponseEntity<ResponseMessage> cancelFriendRequest(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        friendService.cancelFriendRequest(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Muscle friend request cancel successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);

    }

    // 친구 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<ResponseMessage> acceptFriendRequest(HttpServletRequest request, @Valid @RequestBody RequestAuth.FriendRequestDto friendRequestDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        friendService.acceptFriendRequest(token, friendRequestDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Muscle friend request successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    //친구 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<ResponseMessage> rejectFriendRequest(HttpServletRequest request, @Valid @RequestBody RequestAuth.FriendRequestDto friendRequestDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        friendService.rejectFriendRequest(token, friendRequestDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Muscle friend rejected successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);

    }

    // 친구 조회
    @GetMapping("/getFriend")
    public ResponseEntity<ResponseMessage> getFriend (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseAuth.FriendResponseDto response = friendService.getFriend(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    // 보낸 친구 요청 조회
    @GetMapping("/getRequest")
    public ResponseEntity<ResponseMessage> getSendFriendRequest(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseAuth.FriendRecipientResponseDto response = friendService.getSendFriendRequest(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }
    // 친구 요청 목록 조회
    @GetMapping("/getReceived")
    public ResponseEntity<ResponseMessage> getFriendRequestList (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseAuth.FriendRequestResponseDto> response = friendService.getReceivedFriendRequests(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    // 친구 삭제
    @DeleteMapping("/remove")
    public ResponseEntity<ResponseMessage> removeFriend (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        friendService.removeFriend(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Muscle friend removed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }






}
