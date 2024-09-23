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
        private Long postId;
        private String commentContent;

        public static Comment toEntity(CreateCommentDto createCommentDto, Long commentWriterId, Post post) {
            return Comment.builder()
                    .commentWriterId(commentWriterId)
                    .commentContent(createCommentDto.getCommentContent())
                    .post(post)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateCommentDto {
        private Long commentId;
        private String commentContent;

    }
}
