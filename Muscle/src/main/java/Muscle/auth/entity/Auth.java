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

    @Column(name="level")
    private String level;


    @Column(name="salt")
    private String salt;

    @Column(name = "userImg")
    private String userImg;

    @Column(name = "naverId")
    private String naverId;

    // 친구 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muscleFriend_id")
    private Auth muscleFriend;




    @Builder
    public Auth(String email, String password, String name, String muscleId, String salt, String userImg, String naverId){
        this.email = email;
        this.password = password;
        this.name = name;
        this.muscleId = muscleId;
        this.salt = salt;
        this.userImg = userImg;
        this.naverId = naverId;
    }



    public void update(String password, String name, String muscleId, String salt) {
        this.password = password;
        this.name = name;
        this.muscleId = muscleId;
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
