package Muscle.chat.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "chatRoom")
@Entity
@Getter
@NoArgsConstructor
@Data
public class ChatRoom {

    @Id
    private String chatRoomId;

    @Column(name = "roomName")
    private String roomName;

    @Column(name = "senderId")
    private Long senderId;

    @Column(name = "receiverId")
    private Long receiverId;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "lastMessage")
    private String lastMessage;

    @Column(name = "lastMessageTimestamp")
    private String lastMessageTimestamp;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference  // 부모 엔티티에서 자식 엔티티로의 참조 관리
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatRoom(String roomName, Long senderId, Long receiverId, String createdAt) {
        this.roomName = roomName;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
    }

}
