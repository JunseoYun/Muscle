package Muscle.auth.service;


import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.entity.Auth;
import Muscle.auth.entity.Follow;
import Muscle.auth.entity.UserRole;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.repository.FollowRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.auth.security.role.Role;
import Muscle.auth.util.RedisUtil;
import Muscle.auth.util.SHA256Util;
import Muscle.common.dto.ResponseMessage;
import Muscle.common.exception.error.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.*;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final FollowRepository followRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
//    private final S3Service s3Service;
    private final RedisUtil redisUtil;

    @Transactional
    public void registerUser(RequestAuth.RegisterUserDto registerUserDto) {

        Auth user = authRepository.findByEmail(registerUserDto.getEmail());
        if(user != null){

            if(user.getNaverId() != null && user.getPassword() == null) {
                throw new UserAlreadyRegisteredException("Already registered with Naver.");
            }
            throw new ExistingEmailException();
        }
        user = authRepository.findByMuscleId(registerUserDto.getMuscleId());
        if(user != null){
            throw new ExistingNicknameException();
        }

        String salt = SHA256Util.generateSalt();
        String encryptedPassword = SHA256Util.getEncrypt(registerUserDto.getPassword(),salt);
        user = RequestAuth.RegisterUserDto.toEntity(registerUserDto, salt, encryptedPassword);
        authRepository.save(user);
    }

    @Transactional
    public Optional<ResponseAuth.LoginUserRsDto> loginUser(RequestAuth.LoginUserRqDto loginUserRqDto) {
        Auth user = authRepository.findByMuscleId(loginUserRqDto.getMuscleId());
        if(user == null)
            throw new LoginFailedException();

        String salt = user.getSalt();
        user = authRepository.findByMuscleIdAndPassword(loginUserRqDto.getMuscleId(), SHA256Util.getEncrypt(loginUserRqDto.getPassword(),salt));
        if(user == null)
            throw new LoginFailedException();

        String accessToken = createAccessToken(user.getMuscleId());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken));
    }





//    @Transactional
//    public String uploadImg(MultipartFile file, Optional<String> token) {
//        String email = null;
//        if(token.isPresent()){
//            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
//            email = jwtAuthToken.getClaims().getSubject();
//        }
//        Auth user = authRepository.findByEmail(email);
//        String url = "";
//        try {
//            url = s3Service.upload(file,"user");
//        }
//        catch (IOException e){
//            System.out.println("S3 upload failed.");
//        }
//
//        user.setUserImg(url);
//        authRepository.save(user);
//        return url;
//    }

    public String createAccessToken(String userid) {
        Date expiredDate = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        JwtAuthToken accessToken = jwtAuthTokenProvider.createAuthToken(userid, Role.USER.getCode(),expiredDate);
        return accessToken.getToken();
    }


    public String getTempToken(String email, String verificationCode){
        JwtAuthToken tempToken = null;
        if(redisUtil.getData(verificationCode)==null){
            throw new RuntimeException();
        }
        else if(redisUtil.getData(verificationCode).equals(email)){
            Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());
            tempToken = jwtAuthTokenProvider.createAuthToken(email, Role.USER.getCode(),expiredDate);
        }
        return tempToken.getToken();
    }



    @Transactional
    public void updateUser(Optional<String> token, RequestAuth.UpdateUserDto updateUserDto) {

        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByMuscleId(muscleId);
        if(user == null)
            throw new NotFoundUserException();
        Auth nameUser = authRepository.findByMuscleId(updateUserDto.getMuscleId());
        if(nameUser != null && !user.equals(nameUser))
            throw new RegisterFailedException();

        user.update(updateUserDto.getName(), updateUserDto.getMuscleId());

        authRepository.save(user);
    }

    @Transactional
    public void changePassword(Optional<String> token, String password){
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByMuscleId(muscleId);
        if(user == null)
            throw new NotFoundUserException();

        String salt = SHA256Util.generateSalt();
        String encryptedPassword = SHA256Util.getEncrypt(password, salt);
        user.changePassword(encryptedPassword, salt);
        authRepository.save(user);
    }

    @Transactional
    public void setUserLevel(Optional<String> token, RequestAuth.SetUserLevelDto setUserLevelDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        if(user == null)
            throw new NotFoundUserException();
        if(Objects.equals(user.getLevel(), setUserLevelDto.getLevel())) {
            throw new RegisterFailedException();
        }

        user.setLevel(setUserLevelDto.getLevel());

        authRepository.save(user);
    }




    @Transactional
    public ResponseAuth.GetUserDto getUser(Optional<String> token) {

        String muscleId = null;
        if(token.isPresent()){
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        if (user == null)
            throw new NotFoundUserException();

        Auth friend = user.getMuscleFriend();
        return ResponseAuth.GetUserDto.toDto(user, friend);
    }

    public ResponseAuth.GetUserInfoDto getUserInfo (Optional<String> token, Long targetId) {
        String muscleId = null;
        if(token.isPresent()){
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        if (user == null)
            throw new NotFoundUserException();
        Auth target = authRepository.findById(targetId).get();
        Auth targetFriend = target.getMuscleFriend();


        boolean isFollowed = false;
        Follow follow = followRepository.findByFollowerAndFollowing(user, target);
        if (follow != null) {
            isFollowed = true;
        }
        return ResponseAuth.GetUserInfoDto.toDto(target, targetFriend, isFollowed);
    }



    public void remove(Optional<String> token) {
        String muscleId = null;
        if(token.isPresent()){
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByMuscleId(muscleId);
        authRepository.delete(user);

    }

    public String userlinking(Optional<String> token) {
        String muscleId = null;
        if(token.isPresent()){
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByMuscleId(muscleId);
        String linked = null;

        if (user.getPassword() != null && user.getNaverId() != null && user.getKakaoId() != null) {
            linked = "Muscle & Naver & Kakao";  // Muscle, Naver, Kakao 모두 연동된 경우
        }
        else if (user.getPassword() != null && user.getNaverId() != null && user.getKakaoId() == null) {
            linked = "Muscle & Naver";  // Muscle과 Naver만 연동된 경우
        }
        else if (user.getPassword() != null && user.getKakaoId() != null && user.getNaverId() == null) {
            linked = "Muscle & Kakao";  // Muscle과 Kakao만 연동된 경우
        }
        else if(user.getNaverId() != null & user.getKakaoId() != null & user.getPassword() == null) {
            linked = "Naver & Kakao";
        }
        else if (user.getPassword() != null) {
            linked = "Muscle";  // Muscle만 연동된 경우
        }
        else if (user.getNaverId() != null && user.getKakaoId() == null) {
            linked = "Naver";  // Naver만 연동된 경우
        }
        else if (user.getKakaoId() != null && user.getNaverId() == null) {
            linked = "Kakao";  // Kakao만 연동된 경우
        }
        else {
            linked = "Unknown";  // 아무것도 연동되지 않은 경우
        }


        return linked;
    }


    public void setAdmin(Optional<String> token) {
        String muscleId = null;
        if(token.isPresent()){
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }

        Auth user = authRepository.findByMuscleId(muscleId);
        user.setRole(UserRole.ADMIN);
        authRepository.save(user);
    }

    public List<ResponseAuth.SearchUserDto> searchUser(String muscleId) {
        List<Auth> entityList = authRepository.findByMuscleIdContainingOrdered(muscleId);
        List<ResponseAuth.SearchUserDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(auth -> dtoList.add(ResponseAuth.SearchUserDto.toDto(auth)));

        return dtoList;
    }

    //아이디 중복 검사
    public Boolean checkId(String muscleId) {
        boolean isCheckId = false;
        Auth auth = authRepository.findByMuscleId(muscleId);
        if(auth == null) {
            isCheckId = true;
        }
        return isCheckId;
    }


}
