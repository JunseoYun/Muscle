package Muscle.auth.dto;


import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;

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
        private String muscleId;
        private String address;
        private Double latitude;
        private Double longitude;

        public static Auth toEntity(RegisterUserDto registerUserDto, String salt, String encryptedPassword){
            return Auth.builder()
                    .email(registerUserDto.getEmail())
                    .password(encryptedPassword)
                    .name(registerUserDto.getName())
                    .muscleId(registerUserDto.getMuscleId())
                    .address(registerUserDto.getAddress())
                    .latitude(registerUserDto.getLatitude())
                    .longitude(registerUserDto.getLongitude())
                    .salt(salt)
                    .build();
        }
    }

    @Builder
    @Data
    public static class naverRegister {
        private String naverId;
        private String email;
        private String name;
        private String muscleId;

        public static Auth toEntity(String naverId, String email, String name,  String muscleId) {
            return Auth.builder()
                    .naverId(naverId)
                    .email(email)
                    .name(name)
                    .muscleId(muscleId)
                    .build();
        }

    }

    @Builder
    @Data
    public static class kakaoRegister {
        private String kakaoId;
        private String email;
        private String name;
        private String muscleId;

        public static Auth toEntity(String kakaoId, String email, String name, String muscleId) {
            return Auth.builder()
                    .kakaoId(kakaoId)
                    .email(email)
                    .name(name)
                    .muscleId(muscleId)
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginUserRqDto{
        private String muscleId;
        private String password;
    }

    @Builder
    @Data
    public static class UpdateUserDto{
        private String name;
        private String muscleId;
        private String address;
        private Double latitude;
        private Double longitude;

    }

    @Builder
    @Data
    public static class ChangePasswordDto{
        @NotNull
        private String currentPassword;
        private String newPassword;
    }


    @Builder
    @Data
    public static class SetUserLevelDto {
        private String level;
        private String temp;


    }




    @Builder
    @Data
    public static class FriendRequestDto {
        private Long userId;    // 친구 요청을 받을 사람의 ID
        private String temp;


    }

    @Builder
    @Data
    public static class FollowDto {
        private Long userId;
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
