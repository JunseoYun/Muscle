package Muscle.post.dto;


import Muscle.post.entity.Post;
import Muscle.post.entity.PostRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RequestPost {

    @Data
    @Builder
    public static class CreatePostDto {
        private String title;
        private String content;
        private String postRole;
        private String board;

        public static Post toEntity(CreatePostDto createPostDto, Long writerId, PostRole postRole) {
            return Post.builder()
                    .writerId(writerId)
                    .title(createPostDto.getTitle())
                    .postRole(postRole)
                    .board(createPostDto.getBoard())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdatePostDto {
        private Long postId;
        private String title;
        private String content;
    }

    @Data
    @Builder
    public static class SendPostIdDto {
        private Long postId;
        private String temp;
    }
}
