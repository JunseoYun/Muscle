package Muscle.proRequest.entity;

import Muscle.auth.entity.Auth;
import Muscle.post.entity.PostImage;
import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="pro_request")
@Entity
@Getter
@NoArgsConstructor
@Data
public class ProRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 요청한 사용자 (보낸 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Auth requester;

    @Column(name = "proName")
    private String proName;

    @Column(name = "proField")
    private String proField;



    @Column(name = "proGroup")
    private String proGroup;

    @Column(name = "proWorkExp")
    private String proWorkExp;

    @OneToMany(mappedBy = "proRequest", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProCertifyImage> images = new ArrayList<>();

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Builder
    public ProRequest(Auth user, String proName, String proField,  String proGroup, String proWorkExp) {
        this.requester = user;
        this.proName = proName;
        this.proField = proField;
        this.proGroup = proGroup;
        this.proWorkExp = proWorkExp;
        this.requestTime = LocalDateTime.now();
    }
}
