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
        private String nickName;
        private String level;
        private String userImg;

        public static GetUserDto toDto(Auth user){
            return GetUserDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickName(user.getNickName())
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
        private String friendNickName;
        private String friendImg;
        private String friendLevel;

        public static FriendResponseDto toDto(Auth user) {
            return FriendResponseDto.builder()
                    .friendId(user.getId())
                    .friendName(user.getName())
                    .friendNickName(user.getNickName())
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
        private String requesterNickName;
        private String requesterImg;
        private String requesterLevel;

        public static FriendRequestResponseDto toDto(FriendRequest friendRequest) {
            return FriendRequestResponseDto.builder()
                    .requesterId(friendRequest.getRequester().getId())
                    .requesterName(friendRequest.getRequester().getName())
                    .requesterNickName(friendRequest.getRequester().getNickName())
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
        private String recipientNickName;
        private String recipientImg;
        private String recipientLevel;

        public static FriendRecipientResponseDto toDto(FriendRequest friendRequest) {
            return FriendRecipientResponseDto.builder()
                    .recipientId(friendRequest.getRecipient().getId())
                    .recipientName(friendRequest.getRecipient().getName())
                    .recipientNickName(friendRequest.getRecipient().getNickName())
                    .recipientImg(friendRequest.getRecipient().getUserImg())
                    .recipientLevel(friendRequest.getRecipient().getLevel())
                    .build();
        }
    }
}
