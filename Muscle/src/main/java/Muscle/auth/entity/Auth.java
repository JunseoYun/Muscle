package Muscle.auth.entity;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name="muscleId")
    private String muscleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.AMATEUR;

    @Column(name="level")
    private String level = "등급 미정";


    @Column(name="salt")
    private String salt;

    @Column(name = "userImg")
    private String userImg;

    @Column(name = "naverId")
    private String naverId;

    @Column(name = "kakaoId")
    private String kakaoId;

    @Column(name = "postCount")
    private Long postCount = 0L;

    @Column(name = "follower")
    private Long followerCount = 0L;

    @Column(name = "following")
    private Long followingCount = 0L;

    // 친구 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muscleFriend_id")
    private Auth muscleFriend;




    @Builder
    public Auth(String email, String password, String name, String muscleId, String salt, String userImg, String naverId, String kakaoId){
        this.email = email;
        this.password = password;
        this.name = name;
        this.muscleId = muscleId;
        this.salt = salt;
        this.userImg = userImg;
        this.naverId = naverId;
        this.kakaoId = kakaoId;
    }



    public void update(String password, String name, String muscleId, String salt) {
        this.password = password;
        this.name = name;
        this.muscleId = muscleId;
        this.salt = salt;
    }





    public void changePassword(String password, String salt){
        this.password = password;
        this.salt = salt;
    }

    public void increaseFollowerCount(){ this. followerCount++; }
    public void decreaseFollowerCount(){ this. followerCount--; }
    public void increaseFollowingCount(){ this. followingCount++; }
    public void decreaseFollowingCount(){ this. followingCount--; }


}
