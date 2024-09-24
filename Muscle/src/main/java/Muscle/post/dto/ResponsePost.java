package Muscle.post.dto;

import Muscle.post.entity.Post;
import Muscle.auth.entity.Auth;
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


        public static GetPostDto toDto(Auth writer, Post post, Boolean isPostLiked, Boolean isPostSaved) {

            return GetPostDto.builder()
                    .writerId(writer.getId())
                    .writerMuscleId(writer.getMuscleId())
                    .writerLevel(writer.getLevel())
                    .writerImg(writer.getUserImg())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .postImg(post.getPostImg())
                    .isPostLiked(isPostLiked)
                    .isPostSaved(isPostSaved)
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
