package Muscle.auth.dto;


import Muscle.auth.entity.Auth;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RequestAuth {
    @Builder
    @Data
    public static class RegisterUserDto{
        private String email;
        private String password;
        private String name;
        private String nickName;

        public static Auth toEntity(RegisterUserDto registerUserDto, String salt, String encryptedPassword){
            return Auth.builder()
                    .email(registerUserDto.getEmail())
                    .password(encryptedPassword)
                    .name(registerUserDto.getName())
                    .nickName(registerUserDto.getNickName())
                    .salt(salt)
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginUserRqDto{
        private String email;
        private String password;
    }

    @Builder
    @Data
    public static class UpdateUserDto{
        private String password;
        private String name;
        private String nickName;

        public static Auth toEntity(Auth user, UpdateUserDto updateUserDto, String salt, String encryptedPassword){
            user.update(encryptedPassword, updateUserDto.getName(), updateUserDto.getNickName(), salt);
            return user;
        }
    }

    @Builder
    @Data
    public static class ChangePasswordDto{
        @NotNull
        private String password;
        private String temp;
    }

    @Builder
    @Data
    public static class SendEmailDto{
        @Email
        @NotEmpty(message = "Enter email.")
        private String email;
        private String temp;
    }

    @Builder
    @Data
    public static class VerifyEmailDto{
        @Email
        @NotEmpty(message = "Enter email.")
        private String email;
        private String verificationCode;
    }
}
