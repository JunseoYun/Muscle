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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sendFriendRequest_id")
    private Auth sendFriendRequest;


    // 내가 받은 친구 요청 리스트 (1:N 관계)
    @OneToMany(mappedBy = "sendFriendRequest", orphanRemoval = true)
    private List<Auth> friendRequestList = new ArrayList<>();


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

    public void clearFriendRequestList() {
        this.friendRequestList.clear();
    }

    public void setUserLevel(String level) {
        this.level = level;
    }

    public void setFriend(Auth auth) {
        this.muscleFriend = auth;
    }

    public void addFriendRequestList(Auth auth) {
        this.friendRequestList.add(auth);
    }

    public void sendFriendRequest(Auth auth) {
        this.sendFriendRequest = auth;
    }




    public void changePassword(String password, String salt){
        this.password = password;
        this.salt = salt;
    }


}
