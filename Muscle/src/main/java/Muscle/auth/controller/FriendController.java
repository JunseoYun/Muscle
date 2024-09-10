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

    // 친구 요청 목록 조회
    @GetMapping("/getRequests")
    public ResponseEntity<ResponseMessage> getFriendRequestList (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseAuth.FriendRequestResponseDto> response = friendService.getFriendRequestList(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }
}
