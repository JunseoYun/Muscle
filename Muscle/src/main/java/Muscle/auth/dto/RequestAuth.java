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

        public static Auth toEntity(RegisterUserDto registerUserDto, String salt, String encryptedPassword){
            return Auth.builder()
                    .email(registerUserDto.getEmail())
                    .password(encryptedPassword)
                    .name(registerUserDto.getName())
                    .muscleId(registerUserDto.getMuscleId())
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
    public static class LoginUserRqDto{
        private String email;
        private String password;
    }

    @Builder
    @Data
    public static class UpdateUserDto{
        private String password;
        private String name;
        private String muscleId;

        public static Auth toEntity(Auth user, UpdateUserDto updateUserDto, String salt, String encryptedPassword){
            user.update(encryptedPassword, updateUserDto.getName(), updateUserDto.getMuscleId(), salt);
            return user;
        }
    }

    @Builder
    @Data
    public static class SetUserLevelDto {
        private String level;
        private String temp;

        public static Auth toEntity(Auth user, SetUserLevelDto setUserLevelDto){
            user.setUserLevel(setUserLevelDto.getLevel());
            return user;
        }
    }


    @Builder
    @Data
    public static class FriendRequestDto {
        private Long userId;    // 친구 요청을 받을 사람의 ID
        private String temp;

//        public FriendRequestDto(FriendRequestDto friendRequestDto) {
//            this.userId = friendRequestDto.getUserId();
//            this.temp = friendRequestDto.getTemp();
//        }
    }

//    @Builder
//    @Data
//    public static class ReceiveFriendRequestDto {
//        private Long sendUserId;
//        private String temp;
//
//        public static Auth toEntity(Auth receiveUser, ReceiveFriendRequestDto receiveFriendRequestDto) {
//            receiveUser.addFriendRequestList(receiveFriendRequestDto.getSendUserId());
//
//            return receiveUser;
//        }
//    }



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
