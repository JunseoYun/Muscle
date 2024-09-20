package Muscle.proRequest.dto;

import Muscle.auth.entity.Auth;
import Muscle.proRequest.entity.ProRequest;
import lombok.Builder;
import lombok.Data;

public class RequestPro {

    @Builder
    @Data
    public static class ProRequestDto {
        private String proField;
        private String proName;
        private String proGroup;
        private String proWorkExp;
        private String proCertifyImg;

        public static ProRequest toEntity(ProRequestDto proRequestDto, Auth user) {
            return ProRequest.builder()
                    .proField(proRequestDto.getProField())
                    .proName(proRequestDto.getProName())
                    .proGroup(proRequestDto.getProGroup())
                    .proWorkExp(proRequestDto.getProWorkExp())
                    .proCertifyImg(proRequestDto.getProCertifyImg())
                    .user(user)
                    .build();
        }
    }

    @Builder
    @Data
    public static class ProAcceptDto {
        private Long userId;
        private String proField;
    }

    @Builder
    @Data
    public static class ProRejectDto {
        private Long userId;
        private String temp;
    }


}
