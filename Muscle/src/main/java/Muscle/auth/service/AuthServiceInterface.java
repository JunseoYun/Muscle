package Muscle.auth.service;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface AuthServiceInterface {
    void registerUser(RequestAuth.RegisterUserDto registerUserDto);

    Optional<ResponseAuth.LoginUserRsDto> loginUser(RequestAuth.LoginUserRqDto loginUserDto);

//    String uploadImg(MultipartFile file, Optional<String> token);

    String createAccessToken(String userid);

    String getTempToken(String email, String verificationCode);

    void changePassword(Optional<String> token, String password);

    void updateUser(Optional<String> token, RequestAuth.UpdateUserDto updateUserDto);

    ResponseAuth.GetUserDto getUser(Optional<String> token);

    //String createRefreshToken(String userid);
    //Optional<ResponseAuth.Token> updateAccessToken(String token);
}
