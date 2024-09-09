package Muscle.comment.dto;

import Muscle.comment.entity.Comment;
import Muscle.post.entity.Post;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class RequestComment {

    @Data
    @Builder
    public static class CreateCommentDto {
        private String commentContent;
        private LocalDateTime commentDate;
        private Long postId;

        public static Comment toEntity(CreateCommentDto createCommentDto, Long commentWriterId, Post post) {
            return Comment.builder()
                    .commentWriterId(commentWriterId)
                    .commentContent(createCommentDto.getCommentContent())
                    .commentDate(createCommentDto.getCommentDate())
                    .post(post)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateCommentDto {
        private Long commentId;
        private String commentContent;
        private LocalDateTime commentDate;

        public static Comment toEntity(Comment comment, UpdateCommentDto updateCommentDto) {
            comment.update(updateCommentDto.getCommentContent(), updateCommentDto.getCommentDate());
            return comment;
        }
    }
}
