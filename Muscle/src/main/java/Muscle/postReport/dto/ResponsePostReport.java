package Muscle.postReport.dto;

import Muscle.auth.entity.Auth;
import Muscle.postReport.entity.PostReport;
import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;

public class ResponsePostReport {

    @Builder
    @Getter
    public static class GetPostReportDto {
        private Long reporterId;
        private String reporterMuscleId;
        private String reporterLevel;
        private String reporterImg;
        private Long reportId;
        private String reportReason;
        private LocalDateTime reportDate;
        private Long postId;

        public static GetPostReportDto toDto(Auth reporter, PostReport postReport) {
            return GetPostReportDto.builder()
                    .reporterId(reporter.getId())
                    .reporterMuscleId(reporter.getMuscleId())
                    .reporterLevel(reporter.getLevel())
                    .reporterImg(reporter.getUserImg())
                    .reportId(postReport.getReportId())
                    .reportReason(postReport.getReportReason())
                    .reportDate(postReport.getReportDate())
                    .postId(postReport.getPost().getPostId())
                    .build();
        }
    }
}
