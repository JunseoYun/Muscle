package Muscle.comment.dto;


import Muscle.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ResponseComment {

    @Builder
    @Getter
    public static class GetCommentDto {
        private Long commentWriterId;
        private Long commentId;
        private String commentContent;
        private LocalDateTime commentDate;
        private Long postId;

        public static GetCommentDto toDto(Comment comment) {
            return GetCommentDto.builder()
                    .commentWriterId(comment.getCommentWriterId())
                    .commentId(comment.getCommentId())
                    .commentContent(comment.getCommentContent())
                    .commentDate(comment.getCommentDate())
                    .postId(comment.getPost().getPostId())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetAllCommentDto {
        private Long commentWriterId;
        private Long commentId;
        private String commentContent;
        private LocalDateTime commentDate;
        private Long postId;

        public static GetAllCommentDto toDto(Comment comment) {
            return GetAllCommentDto.builder()
                    .commentWriterId(comment.getCommentWriterId())
                    .commentId(comment.getCommentId())
                    .commentContent(comment.getCommentContent())
                    .commentDate(comment.getCommentDate())
                    .postId(comment.getPost().getPostId())
                    .build();
        }
    }
}
