package Muscle.auth.service;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.entity.Auth;
import Muscle.auth.entity.FriendRequest;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.repository.FriendRequestRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.exception.error.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final AuthRepository authRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final FriendRequestRepository friendRequestRepository;


    // 친구 요청 보내기
    @Transactional
    public void sendFriendRequest(Optional<String> token, RequestAuth.FriendRequestDto friendRequestDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }


        Auth sender = authRepository.findByMuscleId(muscleId);
        if (sender.getMuscleFriend() != null) {
            throw new IllegalArgumentException("You have already Muscle Friend.");
        }

        Auth recipient = authRepository.findById(friendRequestDto.getUserId()).get();


        if(sender == recipient) {
            throw new IllegalArgumentException("자신에게 친구 요청 불가");
        }
        if(friendRequestRepository.findByRequester(sender) != null && friendRequestRepository.findByRequester(sender).getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("이미 보낸 친구 요청이 있습니다.");
        } else if (sender.getMuscleFriend() != null) {
            throw new IllegalArgumentException("이미 Muscle 친구가 있습니다.");
        }
        FriendRequest friendRequest = new FriendRequest(sender, recipient);

        friendRequestRepository.save(friendRequest);


    }

    //친구 요청 취소
    @Transactional
    public void cancelFriendRequest(Optional<String> token, Long requestId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth sender = authRepository.findByMuscleId(muscleId);
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).get();
        if(friendRequest == null || !friendRequest.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("취소할 친구 요청이 없습니다.");
        }
        if(friendRequest.getRequester() != sender) {
            throw new IllegalArgumentException("내 친구 요청이 아닙니다.");
        }

        friendRequestRepository.delete(friendRequest);
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Optional<String> token, RequestAuth.FriendRequestDto friendRequestDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth recipient = authRepository.findByMuscleId(muscleId);
        Auth sender = authRepository.findById(friendRequestDto.getUserId()).get();
        FriendRequest friendRequest = friendRequestRepository.findByRequester(sender);
        if(recipient.getMuscleFriend() != null || sender.getMuscleFriend() != null ) {
            throw new IllegalArgumentException("이미 머슬 친구 있음.");
        }
        friendRequest.setStatus("ACCEPTED");
        recipient.setMuscleFriend(sender);
        sender.setMuscleFriend(recipient);


        //친구 요청 받은 후 자신에게 온 친구 요청 리스트 안 지움
        authRepository.save(recipient);
        authRepository.save(sender);



        friendRequestRepository.delete(friendRequest);
    }

    //친구 요청 거절
    @Transactional
    public void rejectFriendRequest(Optional<String> token, RequestAuth.FriendRequestDto friendRequestDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth recipient = authRepository.findByMuscleId(muscleId);
        Auth sender = authRepository.findById(friendRequestDto.getUserId()).get();
        FriendRequest friendRequest = friendRequestRepository.findByRequester(sender);


        friendRequest.setStatus("REJECTED");

        //friendRequestRepository.delete(friendRequest);
    }




    // 친구 목록 조회
    @Transactional(readOnly = true)
    public ResponseAuth.FriendResponseDto getFriend(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);

        Auth friend = user.getMuscleFriend(); // 1대1 친구 관계
        if(friend == null) {
            return null;
        }



        return ResponseAuth.FriendResponseDto.toDto(friend);

    }


    // 보낸 친구 요청 조회
    @Transactional
    public List<ResponseAuth.FriendRecipientResponseDto> getSendFriendRequest(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth sender = authRepository.findByMuscleId(muscleId);

        List<FriendRequest> friendRequests = friendRequestRepository.findAllByRequester(sender);

        return friendRequests.stream()
                .map(ResponseAuth.FriendRecipientResponseDto::toDto)
                .collect(Collectors.toList());
    }

    // 받은 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public List<ResponseAuth.FriendRequestResponseDto> getReceivedFriendRequests(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth recipient  = authRepository.findByMuscleId(muscleId);

        List<FriendRequest> receivedFriendRequests = friendRequestRepository.findAllByRecipientAndStatus(recipient, "PENDING");

        return receivedFriendRequests.stream()
                .map(ResponseAuth.FriendRequestResponseDto::toDto)
                .collect(Collectors.toList());
    }

    // 친구 삭제
    @Transactional
    public void removeFriend(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        Auth friend = user.getMuscleFriend();

        user.setMuscleFriend(null);
        friend.setMuscleFriend(null);

        authRepository.save(user);
        authRepository.save(friend);

    }




}





