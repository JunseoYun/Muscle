package Muscle.chat.dto;

import Muscle.chat.entity.ChatMessage;
import Muscle.chat.entity.ChatRoom;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseChat {

    @Data
    public static class ChatMessageDto {
        private Long senderId;
        private String content;
        private String timestamp;  // LocalDateTime -> String

        public ChatMessageDto(ChatMessage chatMessage) {
            this.senderId = chatMessage.getSenderId();
            this.content = chatMessage.getContent();
            this.timestamp = chatMessage.getTimestamp();  // String 그대로 사용
        }
    }

    @Data
    public static class ChatRoomDto {
        private String roomId;
        private Long senderId;
        private Long receiverId;
        private List<ChatMessageDto> messages;

        public ChatRoomDto(ChatRoom chatRoom) {
            this.roomId = chatRoom.getChatRoomId();
            this.senderId = chatRoom.getSenderId();
            this.receiverId = chatRoom.getReceiverId();
            this.messages = chatRoom.getMessages().stream()
                    .map(ChatMessageDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class ChatRoomListDto {
        private String roomId;
        private String roomName;
        private String senderImg;
        private String receiverImg;
        private String dateTime;  // LocalDateTime -> String

        public ChatRoomListDto(ChatRoom chatRoom, String senderImg, String receiverImg) {
            this.roomId = chatRoom.getChatRoomId();
            this.roomName = chatRoom.getRoomName();
            this.senderImg = senderImg;
            this.receiverImg = receiverImg;
            this.dateTime = chatRoom.getCreatedAt();  // String 그대로 사용
        }
    }
}
