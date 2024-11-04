package Muscle.auth.controller;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.auth.service.FollowService;
import Muscle.common.dto.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<ResponseMessage> follow(HttpServletRequest request, @Valid @RequestBody RequestAuth.FollowDto followDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        followService.follow(token, followDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Follow successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/getFollower")
    public ResponseEntity<ResponseMessage> getFollower (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseAuth.FollowerResponseDto> response = followService.getFollower(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Follower get successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/getUserFollower/{userId}")
    public ResponseEntity<ResponseMessage> getTargetFollower (HttpServletRequest request, @PathVariable("userId") Long userId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseAuth.FollowerResponseDto> response = followService.getUserFollower(token, userId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User follower get successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/getFollowing")
    public ResponseEntity<ResponseMessage> getFollowing (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseAuth.FollowingResponseDto> response = followService.getFollowing(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Following get successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/getUserFollowing/{userId}")
    public ResponseEntity<ResponseMessage> getUserFollowing (HttpServletRequest request, @PathVariable("userId") Long userId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseAuth.FollowingResponseDto> response = followService.getUserFollowing(token, userId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("user following get successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }


    @DeleteMapping("/removeFollower/{userId}")
    public ResponseEntity<ResponseMessage> removeFollower(@PathVariable("userId") Long userId, HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        followService.removeFollower(token, userId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Follower removed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @DeleteMapping("/removeFollowing/{userId}")
    public ResponseEntity<ResponseMessage> removeFollowing(@PathVariable("userId") Long userId, HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        followService.removeFollowing(token, userId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Follower removed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }




}
