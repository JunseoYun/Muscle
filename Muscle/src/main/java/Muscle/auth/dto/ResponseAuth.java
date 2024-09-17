package Muscle.auth.dto;


import Muscle.auth.entity.Auth;
import Muscle.auth.entity.FriendRequest;
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
        private String muscleId;
        private String level;
        private String userImg;

        public static GetUserDto toDto(Auth user){
            return GetUserDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .muscleId(user.getMuscleId())
                    .level(user.getLevel())
                    .userImg(user.getUserImg())
                    .build();
        }
    }

    @Data
    @Builder
    public static class FriendResponseDto {
        private Long friendId;
        private String friendName;
        private String friendMuscleId;
        private String friendImg;
        private String friendLevel;

        public static FriendResponseDto toDto(Auth user) {
            return FriendResponseDto.builder()
                    .friendId(user.getId())
                    .friendName(user.getName())
                    .friendMuscleId(user.getMuscleId())
                    .friendImg(user.getUserImg())
                    .friendLevel(user.getLevel())
                    .build();
        }
    }

    @Data
    @Builder
    public static class FriendRequestResponseDto  {
        private Long requesterId;
        private String requesterName;
        private String requesterMuscleId;
        private String requesterImg;
        private String requesterLevel;

        public static FriendRequestResponseDto toDto(FriendRequest friendRequest) {
            return FriendRequestResponseDto.builder()
                    .requesterId(friendRequest.getRequester().getId())
                    .requesterName(friendRequest.getRequester().getName())
                    .requesterMuscleId(friendRequest.getRequester().getMuscleId())
                    .requesterImg(friendRequest.getRequester().getUserImg())
                    .requesterLevel(friendRequest.getRequester().getLevel())
                    .build();
        }
    }

    @Data
    @Builder
    public static class FriendRecipientResponseDto {
        private Long recipientId;
        private String recipientName;
        private String recipientMuscleId;
        private String recipientImg;
        private String recipientLevel;

        public static FriendRecipientResponseDto toDto(FriendRequest friendRequest) {
            return FriendRecipientResponseDto.builder()
                    .recipientId(friendRequest.getRecipient().getId())
                    .recipientName(friendRequest.getRecipient().getName())
                    .recipientMuscleId(friendRequest.getRecipient().getMuscleId())
                    .recipientImg(friendRequest.getRecipient().getUserImg())
                    .recipientLevel(friendRequest.getRecipient().getLevel())
                    .build();
        }
    }

    @Data
    @Builder
    public static class OauthResponseDto {
        private String naverId;
        private String name;
        private String email;

        public OauthResponseDto(String naverId, String name, String email) {
            this.naverId = naverId;
            this.name = name;
            this.email = email;

        }
    }
}
