package Muscle.proRequest.dto;

import Muscle.post.entity.PostImage;
import Muscle.proRequest.entity.ProCertifyImage;
import Muscle.proRequest.entity.ProRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

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
        private String status;
        private List<String> proCertifyImg;
        private LocalDateTime requestTime;

        public static ProRequesterDto toDto(ProRequest proRequest) {

            List<String> imageUrls = proRequest.getImages().stream()
                    .map(ProCertifyImage::getUrl)
                    .collect(Collectors.toList());

            return ProRequesterDto.builder()
                    .requesterId(proRequest.getRequester().getId())
                    .requesterMuscleId(proRequest.getRequester().getMuscleId())
                    .requesterImg(proRequest.getRequester().getUserImg())
                    .proName(proRequest.getProName())
                    .proField(proRequest.getProField())
                    .proGroup(proRequest.getProGroup())
                    .proWorkExp(proRequest.getProWorkExp())
                    .status(proRequest.getStatus())
                    .proCertifyImg(imageUrls)
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
        private String status;
        private LocalDateTime requestTime;

        public static ProRequestListDto toDto(ProRequest proRequest) {
            return ProRequestListDto.builder()
                    .proRequestId(proRequest.getId())
                    .requesterId(proRequest.getRequester().getId())
                    .requesterMuscleId(proRequest.getRequester().getMuscleId())
                    .requesterImg(proRequest.getRequester().getUserImg())
                    .proName(proRequest.getProName())
                    .proField(proRequest.getProField())
                    .status(proRequest.getStatus())
                    .requestTime(proRequest.getRequestTime())
                    .build();
        }
    }
}
