package Muscle.post.dto;

import Muscle.post.entity.Post;
import Muscle.auth.entity.Auth;
import Muscle.post.entity.PostRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ResponsePost {

    @Getter
    @Builder
    public static class GetPostDto {
        private Long writerId;
        private String writerMuscleId;
        private String writerLevel;
        private String writerImg;
        private PostRole postRole;
        private Long postId;
        private String title;
        private String content;
        private String board;
        private LocalDateTime postDate;
        private Long likeCount;
        private Long commentCount;
        private String postImg;
        private Boolean isPostLiked;
        private Boolean isPostSaved;
        private Boolean isFollowed;


        public static GetPostDto toDto(Auth writer, Post post, Boolean isPostLiked, Boolean isPostSaved, Boolean isFollowed) {

            return GetPostDto.builder()
                    .writerId(writer.getId())
                    .writerMuscleId(writer.getMuscleId())
                    .writerLevel(writer.getLevel())
                    .writerImg(writer.getUserImg())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .postRole(post.getPostRole())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .postImg(post.getPostImg())
                    .isPostLiked(isPostLiked)
                    .isPostSaved(isPostSaved)
                    .isFollowed(isFollowed)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetPostSimpleDto {
        private Long writerId;
        private String writerMuscleId;
        private String writerLevel;
        private String writerImg;
        private Long postId;
        private String title;
        public static GetPostSimpleDto toDto(Auth writer, Post post) {

            return GetPostSimpleDto.builder()
                    .writerId(writer.getId())
                    .writerMuscleId(writer.getMuscleId())
                    .writerLevel(writer.getLevel())
                    .writerImg(writer.getUserImg())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class GetReportPostListDto {
        private Long writerId;
        private String writerMuscleId;
        private String writerLevel;
        private String writerImg;
        private Long postId;
        private String title;
        private PostRole postRole;
        private String board;
        private LocalDateTime postDate;
        private Long reportCount;
        private Long likeCount;
        private Long commentCount;
        public static GetReportPostListDto toDto(Auth writer, Post post) {

            return GetReportPostListDto.builder()
                    .writerId(writer.getId())
                    .writerMuscleId(writer.getMuscleId())
                    .writerLevel(writer.getLevel())
                    .writerImg(writer.getUserImg())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .postRole(post.getPostRole())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .reportCount(post.getReportCount())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetReportPostDto {
        private Long writerId;
        private String writerMuscleId;
        private String writerLevel;
        private String writerImg;
        private Long postId;
        private String title;
        private String content;
        private String board;
        private LocalDateTime postDate;
        private Long reportCount;
        private Long likeCount;
        private Long commentCount;
        private String postImg;

        public static GetReportPostDto toDto(Auth writer, Post post) {

            return GetReportPostDto.builder()
                    .writerId(writer.getId())
                    .writerMuscleId(writer.getMuscleId())
                    .writerLevel(writer.getLevel())
                    .writerImg(writer.getUserImg())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .reportCount(post.getReportCount())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .postImg(post.getPostImg())
                    .build();
        }
    }
    
}
