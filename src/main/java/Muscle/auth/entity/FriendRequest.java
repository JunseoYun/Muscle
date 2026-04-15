package Muscle.auth.entity;


import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="friend_request")
@Entity
@Getter
@NoArgsConstructor
@Data
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 요청한 사용자 (보낸 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Auth requester;

    // 요청받은 사용자 (받은 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Auth recipient;

    @Column(name = "status")
    private String status = "PENDING"; // 기본값은 PENDING

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    // 생성자
    public FriendRequest(Auth requester, Auth recipient) {
        this.requester = requester;
        this.recipient = recipient;
        this.requestTime = LocalDateTime.now();
    }
}

