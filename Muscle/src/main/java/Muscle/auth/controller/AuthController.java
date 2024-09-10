package Muscle.auth.controller;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.auth.service.AuthService;
import Muscle.auth.service.EmailService;
import Muscle.common.dto.ResponseDto;
import Muscle.common.dto.ResponseMessage;
import Muscle.common.exception.error.LoginFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final EmailService emailService;


    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@Valid @RequestBody RequestAuth.RegisterUserDto registerUserDto) {
        authService.registerUser(registerUserDto);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User registered successfully.")
                .build();
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



    @PutMapping("/setUserLevel")
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

//    @PostMapping("/uploadImg")
//    public ResponseEntity<ResponseDto> uploadUserImg(@RequestPart(value = "file", required = false) MultipartFile file, HttpServletRequest request){
//        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
//        String url = authService.uploadImg(file, token);
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Image uploaded successfully.")
//                .data(url)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }





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

}
