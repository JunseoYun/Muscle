package Muscle.chat.dto;

import Muscle.auth.entity.Auth;
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
    public static class ChatRoomIdDto {
        private String roomId;
        private Long senderId;
        private Long receiverId;
        private String userImg;
        private String userMuscleId;
        private String otherUserImg;
        private String otherUserMuscleId;
        private List<ChatMessageDto> messages;

        public ChatRoomIdDto(ChatRoom chatRoom, Auth user, Auth otherUser) {
            this.roomId = chatRoom.getChatRoomId();
            this.senderId = chatRoom.getSenderId();
            this.receiverId = chatRoom.getReceiverId();
            this.userImg = user.getUserImg();
            this.userMuscleId = user.getMuscleId();
            this.otherUserImg = otherUser.getUserImg();
            this.otherUserMuscleId = otherUser.getMuscleId();
            this.messages = chatRoom.getMessages().stream()
                    .map(ChatMessageDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class ChatRoomListDto {
        private String roomId;
        private String roomName;
        private String otherUserImg;
        private String otherUserMuscleId;
        private String lastMessage;
        private String lastMessageTimestamp;  // LocalDateTime -> String

        public ChatRoomListDto(ChatRoom chatRoom, Auth otherUser) {
            this.roomId = chatRoom.getChatRoomId();
            this.roomName = chatRoom.getRoomName();
            this.otherUserImg = otherUser.getUserImg();
            this.otherUserMuscleId = otherUser.getMuscleId();
            this.lastMessage =  chatRoom.getLastMessage();
            this.lastMessageTimestamp = chatRoom.getLastMessageTimestamp();  // String 그대로 사용
        }
    }
}
