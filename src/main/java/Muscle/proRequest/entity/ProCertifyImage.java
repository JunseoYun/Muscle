package Muscle.proRequest.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Table(name = "proCertifyImage")
@Entity
@Getter
@NoArgsConstructor
@Data
public class ProCertifyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proCertifyImageId;

    private String url;  // S3에 저장된 파일의 URL
    private String fileName;  // S3에 저장된 파일의 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proRequest_id", nullable = false)
    private ProRequest proRequest;
}
