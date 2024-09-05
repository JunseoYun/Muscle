package Muscle.auth.dto;


import Muscle.auth.entity.Auth;
import lombok.Builder;
import lombok.Data;

public class ResponseAuth {
    @Data
    @Builder
    public static class LoginUserRsDto{
        private String accessToken;

        public static LoginUserRsDto toDto(String accessToken){
            return LoginUserRsDto.builder()
                    .accessToken(accessToken)
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetUserDto{
        private String email;
        private String name;
        private String nickName;
        private String userImg;

        public static GetUserDto toDto(Auth user){
            return GetUserDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickName(user.getNickName())
                    .userImg(user.getUserImg())
                    .build();
        }
    }
}
