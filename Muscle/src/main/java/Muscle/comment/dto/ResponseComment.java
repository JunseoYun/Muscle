package Muscle.comment.dto;


import Muscle.auth.entity.Auth;
import Muscle.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ResponseComment {

    @Builder
    @Getter
    public static class GetCommentDto {
        private Long writerId;
        private String writerMuscleId;
        private String writerLevel;
        private String writerImg;
        private Long commentId;
        private String commentContent;
        private LocalDateTime commentDate;
        private Long postId;
        private Boolean isMine;

        public static GetCommentDto toDto(Auth writer, Comment comment, Boolean isMine) {
            return GetCommentDto.builder()
                    .writerId(writer.getId())
                    .writerMuscleId(writer.getMuscleId())
                    .writerLevel(writer.getLevel())
                    .writerImg(writer.getUserImg())
                    .commentId(comment.getCommentId())
                    .commentContent(comment.getCommentContent())
                    .commentDate(comment.getCommentDate())
                    .postId(comment.getPost().getPostId())
                    .isMine(isMine)
                    .build();
        }
    }


}
