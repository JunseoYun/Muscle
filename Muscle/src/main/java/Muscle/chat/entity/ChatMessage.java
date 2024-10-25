package Muscle.chat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Table(name = "chatMessage")
@Entity
@Getter
@NoArgsConstructor
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @Column(name = "sender")
    private String sender;

    @Column(name = "content")
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private MessageType messageType;

    @Column(name = "timestamp")
    private String timestamp;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    @JsonBackReference  // 자식 엔티티에서 부모 엔티티로의 참조 관리
    private ChatRoom chatRoom;

    // 생성자
    public ChatMessage(
            @JsonProperty("chatMessageId") Long chatMessageId,
            @JsonProperty("sender") String sender,
            @JsonProperty("content") String content,
            @JsonProperty("messageType") MessageType messageType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("chatRoom") ChatRoom chatRoom
    ) {
        this.chatMessageId = chatMessageId;
        this.sender = sender;
        this.content = content;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.chatRoom = chatRoom;
    }
}
