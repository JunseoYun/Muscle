package Muscle.proRequest.dto;

import Muscle.proRequest.entity.ProRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.PrimitiveIterator;

public class ResponsePro {

    @Data
    @Builder
    public static class ProRequesterDto {
        private Long requesterId;
        private String requesterMuscleId;
        private String requesterImg;
        private String proName;
        private String proField;
        private String proGroup;
        private String proWorkExp;
        private String proCertifyImg;
        private LocalDateTime requestTime;

        public static ProRequesterDto toDto(ProRequest proRequest) {
            return ProRequesterDto.builder()
                    .requesterId(proRequest.getRequester().getId())
                    .requesterMuscleId(proRequest.getRequester().getMuscleId())
                    .requesterImg(proRequest.getRequester().getUserImg())
                    .proName(proRequest.getProName())
                    .proField(proRequest.getProField())
                    .proGroup(proRequest.getProGroup())
                    .proWorkExp(proRequest.getProWorkExp())
                    .proCertifyImg(proRequest.getProCertifyImg())
                    .requestTime(proRequest.getRequestTime())
                    .build();
        }

    }

    @Data
    @Builder
    public static class ProRequestListDto {
        private Long proRequestId;
        private Long requesterId;
        private String requesterMuscleId;
        private String requesterImg;
        private String proName;
        private String proField;
        private LocalDateTime requestTime;

        public static ProRequestListDto toDto(ProRequest proRequest) {
            return ProRequestListDto.builder()
                    .proRequestId(proRequest.getId())
                    .requesterId(proRequest.getRequester().getId())
                    .requesterMuscleId(proRequest.getRequester().getMuscleId())
                    .requesterImg(proRequest.getRequester().getUserImg())
                    .proName(proRequest.getProName())
                    .proField(proRequest.getProField())
                    .requestTime(proRequest.getRequestTime())
                    .build();
        }
    }
}
