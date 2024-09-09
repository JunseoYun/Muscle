package Muscle.auth.entity;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name="auth")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="name")
    private String name;

    @Column(name="nickName")
    private String nickName;

    @Column(name="level")
    private String level;


    @Column(name="salt")
    private String salt;

    @Column(name = "userImg")
    private String userImg;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muscleFriend_id")
    private Auth muscleFriend;

    // 친구 요청 상태 (수락/대기/거절 등)
    @Enumerated(EnumType.STRING)
    private FriendshipStatus friendshipStatus;

    // 친구가 된 날짜
    private LocalDateTime friendshipDate;

    @Builder
    public Auth(String email, String password, String name, String nickName, String salt, String userImg){
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.salt = salt;
        this.userImg = userImg;
    }

    public void update(String password, String name, String nickName, String salt) {
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.salt = salt;
    }

    public void setUserLevel(String level) {
        this.level = level;
    }


    public void changePassword(String password, String salt){
        this.password = password;
        this.salt = salt;
    }


}
