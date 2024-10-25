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

@Table(name = "chatRoom")
@Entity
@Getter
@NoArgsConstructor
@Data
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Column(name = "roomName")
    private String roomName;

    @Column(name = "senderId")
    private Long senderId;

    @Column(name = "receiverId")
    private Long receiverId;

    @Column(name = "createdAt")
    private String createdAt;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference  // 부모 엔티티에서 자식 엔티티로의 참조 관리
    private List<ChatMessage> messages = new ArrayList<>();
}
