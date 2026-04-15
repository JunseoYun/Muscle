package Muscle.postReport.dto;

import Muscle.post.entity.Post;
import Muscle.postReport.entity.PostReport;
import lombok.Builder;
import lombok.Data;

public class RequestPostReport {

    @Data
    @Builder
    public static class CreatePostReportDto {
        private Long postId;
        private String reportReason;

        public static PostReport toEntity(CreatePostReportDto createPostReportDto, Long reporterId, Post post) {
            return PostReport.builder()
                    .reporterId(reporterId)
                    .reportReason(createPostReportDto.getReportReason())
                    .post(post)
                    .build();
        }
    }
}
