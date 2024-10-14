package Muscle.auth.controller;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.auth.service.AuthService;
import Muscle.auth.service.EmailService;
import Muscle.auth.service.KakaoLoginService;
import Muscle.auth.service.NaverLoginService;
import Muscle.common.dto.ResponseDto;
import Muscle.common.dto.ResponseMessage;
import Muscle.common.exception.error.LoginFailedException;
import Muscle.common.exception.error.UserAlreadyRegisteredException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final EmailService emailService;
    private final NaverLoginService naverLoginService;
    private final KakaoLoginService kakaoLoginService;


    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@Valid @RequestBody RequestAuth.RegisterUserDto registerUserDto) {
        try {
            authService.registerUser(registerUserDto);
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message("User registered successfully.")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } catch (UserAlreadyRegisteredException ex) {
            // 이미 등록된 경우 409 Conflict 반환
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message(ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessage);
        } catch (Exception ex) {
            // 다른 예외 처리
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message("An error occurred while registering the user.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @GetMapping("/login/naver")
    public ResponseEntity<ResponseMessage> naverLogin () {
        String naverLoginUrl = naverLoginService.generateNaverLoginUrl();
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Naver login requested successfully.")
                .data(naverLoginUrl)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);

    }


    @GetMapping("/login/oauth2/code/naver")
    public ResponseEntity<ResponseMessage> callbackNaverLogin (@RequestParam String code, @RequestParam String state) {
        ResponseMessage responseMessage = naverLoginService.processNaverLogin(code, state);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<ResponseMessage> kakaoLogin() {
        String kakaoLoginUrl = kakaoLoginService.generateKakaoLoginUrl();
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Kakao login requested successfully.")
                .data(kakaoLoginUrl)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<ResponseMessage> callbackKakaoLogin (@RequestParam String code, @RequestParam String state) {
        ResponseMessage responseMessage = kakaoLoginService.processKakaoLogin(code, state);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> loginUser(@Valid @RequestBody RequestAuth.LoginUserRqDto loginUserRqDto) {
        ResponseAuth.LoginUserRsDto response = authService.loginUser(loginUserRqDto).orElseThrow(() -> new LoginFailedException());
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User logged in successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }



    @PostMapping("/setUserLevel")
    public ResponseEntity<ResponseMessage> setUserLevel(HttpServletRequest request, @Valid @RequestBody RequestAuth.SetUserLevelDto setUserLevelDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        authService.setUserLevel(token, setUserLevelDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User level set successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }



    @PostMapping ("/sendEmail")
    public String sendEmail(@RequestBody @Valid RequestAuth.SendEmailDto sendEmailDto){
        System.out.println("이메일 인증 이메일 :"+sendEmailDto.getEmail());
        return emailService.writeEmail(sendEmailDto.getEmail());
    }



    @PostMapping("/verifyEmail")
    public ResponseEntity<ResponseMessage> verifyEmail(@RequestBody @Valid RequestAuth.VerifyEmailDto verifyEmailDto){
        boolean Checked=emailService.verifyEmail(verifyEmailDto.getEmail(),verifyEmailDto.getVerificationCode());
        if(Checked){
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message("Email verified successfully.")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        }
        else{
            throw new NullPointerException("Verification failed.");
        }
    }

    @PostMapping("/getTempToken")
    public ResponseEntity<ResponseMessage> getTempToken(@RequestBody @Valid RequestAuth.VerifyEmailDto verifyEmailDto){
        String tempToken = authService.getTempToken(verifyEmailDto.getEmail(), verifyEmailDto.getVerificationCode());
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("TempToken issued successfully.")
                .data(tempToken)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @PostMapping("/uploadImg")
    public ResponseEntity<ResponseDto> uploadUserImg(@RequestPart(value = "file", required = false) MultipartFile file, HttpServletRequest request) throws IOException {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        String url = authService.uploadImg(file, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .data(url)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/deleteImg")
    public ResponseEntity<ResponseDto> deleteImg(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        authService.deleteImg(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }





    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateUser(HttpServletRequest request, @Valid @RequestBody RequestAuth.UpdateUserDto updateUserDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        authService.updateUser(token, updateUserDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User information updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }



    @PutMapping("/changePassword")
    public ResponseEntity<ResponseMessage> changePassword(HttpServletRequest request, @Valid @RequestBody RequestAuth.ChangePasswordDto changePasswordDto){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        authService.changePassword(token, changePasswordDto.getPassword());
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Password changed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }


    @GetMapping("/get")
    public ResponseEntity<ResponseMessage> getUser(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseAuth.GetUserDto response = authService.getUser(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/getUserInfo/{targetId}")
    public ResponseEntity<ResponseMessage> getUserInfo(HttpServletRequest request, @PathVariable("targetId") Long targetId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponseAuth.GetUserInfoDto response = authService.getUserInfo(token, targetId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Target information retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseDto> remove (HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        authService.remove(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User deleted successfully. ")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/userlinking")
    public ResponseEntity<ResponseDto> userlinking(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        String linked = authService.userlinking(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Account linking list. ")
                .data(linked)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("setAdmin")
    public ResponseEntity<ResponseMessage> setAdmin(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        authService.setAdmin(token);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Set admin successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/search/{muscleId}")
    public ResponseEntity<ResponseDto>  searchUser(@PathVariable("muscleId") String muscleId) {
        List<ResponseAuth.SearchUserDto> response = authService.searchUser(muscleId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User searched successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //아이디 중복 검사
    @PostMapping("/checkId/{muscleId}")
    public ResponseEntity<ResponseDto> checkId(@PathVariable("muscleId") String muscleId) {
        Boolean response = authService.checkId(muscleId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("MuscleId checked successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //아이디 찾기
    @GetMapping("/findMuscleId/{name}/{email}")
    public ResponseEntity<ResponseDto> findMuscleId(@PathVariable("name") String name, @PathVariable("email") String email) {
        String muscleId = authService.findMuscleId(name, email);
        ResponseDto responseDto = ResponseDto.builder()
                .message("MuscleId found successfully.")
                .data(muscleId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //비밀번호 찾기(임시 비밀번호 부여)
    @GetMapping("/findPassword/{muscleId}/{name}/{email}")
    public ResponseEntity<ResponseDto> findPassword(@PathVariable("muscleId") String muscleId, @PathVariable("name") String name, @PathVariable("email") String email) {
        String tempPassword = authService.findPassword(muscleId, name, email);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Muscle password found successfully.")
                .data(tempPassword)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


}
