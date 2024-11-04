package Muscle.auth.dto;


import Muscle.auth.entity.Auth;
import Muscle.auth.entity.Follow;
import Muscle.auth.entity.FriendRequest;
import Muscle.auth.entity.UserRole;
import jdk.jfr.DataAmount;
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
        private Long userId;
        private String userEmail;
        private String userMuscleId;
        private String userName;
        private String userLevel;
        private String userImg;
        private UserRole userRole;
        private Long userPostCount;
        private Long userFollowerCount;
        private Long userFollowingCount;
        private Long friendId;
        private String friendMuscleId;
        private String friendLevel;
        private String friendImg;

        public static GetUserDto toDto(Auth user, Auth friend){
            if(friend == null) {
                return GetUserDto.builder()
                        .userId(user.getId())
                        .userEmail(user.getEmail())
                        .userMuscleId(user.getMuscleId())
                        .userName(user.getName())
                        .userLevel(user.getLevel())
                        .userImg(user.getUserImg())
                        .userRole(user.getRole())
                        .userPostCount(user.getPostCount())
                        .userFollowerCount(user.getFollowerCount())
                        .userFollowingCount(user.getFollowingCount())
                        .build();
            }
            return GetUserDto.builder()
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .userMuscleId(user.getMuscleId())
                    .userName(user.getName())
                    .userLevel(user.getLevel())
                    .userImg(user.getUserImg())
                    .userRole(user.getRole())
                    .userPostCount(user.getPostCount())
                    .userFollowerCount(user.getFollowerCount())
                    .userFollowingCount(user.getFollowingCount())
                    .friendId(friend.getId())
                    .friendMuscleId(friend.getMuscleId())
                    .friendLevel(friend.getLevel())
                    .friendImg(friend.getUserImg())
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetUserInfoDto {
        private Long userId;
        private String userMuscleId;
        private String userName;
        private String userLevel;
        private String userImg;
        private UserRole userRole;
        private Long userPostCount;
        private Long userFollowerCount;
        private Long userFollowingCount;
        private boolean isFollowed;
        private Long friendId;
        private String friendMuscleId;
        private String friendLevel;
        private String friendImg;

        public static GetUserInfoDto toDto(Auth user, Auth friend, Boolean isFollowed) {
            if(friend == null) {
                return GetUserInfoDto.builder()
                        .userId(user.getId())
                        .userMuscleId(user.getMuscleId())
                        .userName(user.getName())
                        .userLevel(user.getLevel())
                        .userImg(user.getUserImg())
                        .userRole(user.getRole())
                        .userPostCount(user.getPostCount())
                        .userFollowerCount(user.getFollowerCount())
                        .userFollowingCount(user.getFollowingCount())
                        .isFollowed(isFollowed)
                        .build();
            }
            return GetUserInfoDto.builder()
                    .userId(user.getId())
                    .userMuscleId(user.getMuscleId())
                    .userName(user.getName())
                    .userLevel(user.getLevel())
                    .userImg(user.getUserImg())
                    .userRole(user.getRole())
                    .userPostCount(user.getPostCount())
                    .userFollowerCount(user.getFollowerCount())
                    .userFollowingCount(user.getFollowingCount())
                    .isFollowed(isFollowed)
                    .friendId(friend.getId())
                    .friendMuscleId(friend.getMuscleId())
                    .friendLevel(friend.getLevel())
                    .friendImg(friend.getUserImg())
                    .build();
        }
    }

    @Data
    @Builder
    public static class SearchUserDto {
        private Long userId;
        private String muscleId;
        private String level;
        private String userImg;
        private UserRole userRole;

        public static SearchUserDto toDto(Auth user) {
            return SearchUserDto.builder()
                    .userId(user.getId())
                    .muscleId(user.getMuscleId())
                    .level(user.getLevel())
                    .userImg(user.getUserImg())
                    .userRole(user.getRole())
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
        private String status;

        public static FriendRecipientResponseDto toDto(FriendRequest friendRequest) {
            return FriendRecipientResponseDto.builder()
                    .recipientId(friendRequest.getRecipient().getId())
                    .recipientName(friendRequest.getRecipient().getName())
                    .recipientMuscleId(friendRequest.getRecipient().getMuscleId())
                    .recipientImg(friendRequest.getRecipient().getUserImg())
                    .recipientLevel(friendRequest.getRecipient().getLevel())
                    .status(friendRequest.getStatus())
                    .build();
        }
    }

    @Data
    @Builder
    public static class OauthResponseDto {
        private String oauthId;
        private String name;
        private String email;

        public OauthResponseDto(String oauthId, String name, String email) {
            this.oauthId = oauthId;
            this.name = name;
            this.email = email;

        }
    }

    @Data
    @Builder
    public static class FollowerResponseDto {
        private Long followerId;
        private String followerName;
        private String followerMuscleId;
        private String followerImg;
        private String followerLevel;

        public static FollowerResponseDto toDto(Follow follow) {
            return FollowerResponseDto.builder()
                    .followerId(follow.getFollower().getId())
                    .followerName(follow.getFollower().getName())
                    .followerMuscleId(follow.getFollower().getMuscleId())
                    .followerImg(follow.getFollower().getUserImg())
                    .followerLevel(follow.getFollower().getLevel())
                    .build();
        }
    }

    @Data
    @Builder
    public static class FollowingResponseDto {
        private Long followingId;
        private String followingName;
        private String followingMuscleId;
        private String followingImg;
        private String followingLevel;

        public static FollowingResponseDto toDto(Follow follow) {
            return FollowingResponseDto.builder()
                    .followingId(follow.getFollowing().getId())
                    .followingName(follow.getFollowing().getName())
                    .followingMuscleId(follow.getFollowing().getMuscleId())
                    .followingImg(follow.getFollowing().getUserImg())
                    .followingLevel(follow.getFollowing().getLevel())
                    .build();
        }
    }
}
