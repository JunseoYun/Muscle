package Muscle.auth.service;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
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



    @Transactional
    public void sendFriendRequest(Optional<String> token, RequestAuth.FriendRequestDto friendRequestDto) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth requester = authRepository.findByEmail(email);
        Auth target = authRepository.findById(friendRequestDto.getUserId()).get();

        requester.sendFriendRequest(target);
        target.addFriendRequestList(requester);

        authRepository.save(requester);
        authRepository.save(target);

    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Optional<String> token, RequestAuth.FriendRequestDto friendRequestDto) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth target = authRepository.findByEmail(email);
        Auth requester = authRepository.findById(friendRequestDto.getUserId()).get();

        target.setFriend(requester); // 친구 관계 설정
        requester.setFriend(target);

        // 친구 요청 리스트에서 제거 (수락했으므로)
        target.getFriendRequestList().remove(requester);

        authRepository.save(target);
        authRepository.save(requester);
    }

    // 친구 목록 조회
    @Transactional(readOnly = true)
    public ResponseAuth.FriendResponseDto getFriend(Optional<String> token) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByEmail(email);

        Auth friend = user.getMuscleFriend(); // 1대1 친구 관계

        return ResponseAuth.FriendResponseDto.toDto(friend);

    }




    // 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public List<ResponseAuth.FriendRequestResponseDto> getFriendRequestList(Optional<String> token) {
        String email = null;
        List<ResponseAuth.FriendRequestResponseDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByEmail(email);

        // 친구 요청 리스트에서 요청자 정보 가져오기
        user.getFriendRequestList().forEach(requester -> {
            dtoList.add(ResponseAuth.FriendRequestResponseDto.toDto(requester));
        });

        return dtoList;
    }









}
