package Muscle.post.dto;


import Muscle.post.entity.Post;
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
        private String board;
        private LocalDateTime postDate;

        public static Post toEntity(CreatePostDto createPostDto, Long writerId) {
            return Post.builder()
                    .writerId(writerId)
                    .title(createPostDto.getTitle())
                    .board(createPostDto.getBoard())
                    .postDate(createPostDto.getPostDate())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdatePostDto {
        private Long postId;
        private String title;
        private String content;
        private LocalDateTime postDate;

        public static Post toEntity(Post post, UpdatePostDto updatePostDto) {
            post.update(updatePostDto.getTitle(), updatePostDto.getContent(), updatePostDto.getPostDate());
            return post;
        }
    }
}
