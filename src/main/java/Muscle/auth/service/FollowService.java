package Muscle.auth.service;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.entity.Auth;
import Muscle.auth.entity.Follow;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.repository.FollowRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final AuthRepository authRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final FollowRepository followRepository;


    //상대를 팔로우 하기
    @Transactional
    public void follow(Optional<String> token, RequestAuth.FollowDto followDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }

        Auth follower = authRepository.findByMuscleId(muscleId);
        Auth following = authRepository.findById(followDto.getUserId()).get();
        if(follower == following) {
            throw new IllegalArgumentException("Don't follow myself");
        }

        Follow follow = new Follow(follower, following);
        follower.increaseFollowingCount();
        following.increaseFollowerCount();

        authRepository.save(follower);
        authRepository.save(following);
        followRepository.save(follow);
    }

    //나를 팔로우 하는 사람 조회
    @Transactional
    public List<ResponseAuth.FollowerResponseDto> getFollower (Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth following = authRepository.findByMuscleId(muscleId);

        List<Follow> followerList = followRepository.findByFollowing(following);

        return followerList.stream()
                .map(ResponseAuth.FollowerResponseDto::toDto)
                .collect(Collectors.toList());

    }

    //특정 유저를 팔로우 하는 사람 조회
    @Transactional
    public List<ResponseAuth.FollowerResponseDto> getUserFollower (Optional<String> token, Long userId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth auth = authRepository.findByMuscleId(muscleId);

        if(auth == null) {
            throw new IllegalArgumentException("Not Muscle user");
        }
        Auth following = authRepository.findById(userId).get();

        List<Follow> followerList = followRepository.findByFollowing(following);

        return followerList.stream()
                .map(ResponseAuth.FollowerResponseDto::toDto)
                .collect(Collectors.toList());

    }

    //내가 팔로우 하는 사람 조회
    @Transactional
    public List<ResponseAuth.FollowingResponseDto> getFollowing (Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth follower = authRepository.findByMuscleId(muscleId);

        List<Follow> followList = followRepository.findByFollower(follower);

        return followList.stream()
                .map(ResponseAuth.FollowingResponseDto::toDto)
                .collect(Collectors.toList());
    }

    //특정 유저가 팔로우 하는 사람 조회
    @Transactional
    public List<ResponseAuth.FollowingResponseDto> getUserFollowing (Optional<String> token, Long userId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth auth = authRepository.findByMuscleId(muscleId);
        if(auth == null) {
            throw new IllegalArgumentException("Not Muscle user");
        }
        Auth follower = authRepository.findById(userId).get();

        List<Follow> followList = followRepository.findByFollower(follower);

        return followList.stream()
                .map(ResponseAuth.FollowingResponseDto::toDto)
                .collect(Collectors.toList());
    }

    //내 팔로우 목록 중 삭제
    @Transactional
    public void removeFollower(Optional<String> token, Long userId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth following = authRepository.findByMuscleId(muscleId);
        Auth follower = authRepository.findById(userId).get();

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following);

        following.decreaseFollowerCount();
        follower.decreaseFollowingCount();

        authRepository.save(following);
        authRepository.save(follower);

        followRepository.delete(follow);
    }


    //내 팔로잉 목록 중 삭제
    @Transactional
    public void removeFollowing(Optional<String> token, Long userId) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth follower = authRepository.findByMuscleId(muscleId);
        Auth following = authRepository.findById(userId).get();

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following);

        following.decreaseFollowerCount();
        follower.decreaseFollowingCount();

        authRepository.save(following);
        authRepository.save(follower);

        followRepository.delete(follow);
    }

}
