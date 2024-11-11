package Muscle.chat.controller;

import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.chat.dto.ResponseChat;
import Muscle.chat.entity.ChatMessage;
import Muscle.chat.entity.ChatRoom;
import Muscle.chat.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final ChatService chatService;

    /**
     * 메시지를 보낼 때 채팅방이 없으면 생성하고 메시지를 전송
     */
    @MessageMapping("/chat.sendMessage/{senderId}/{receiverId}")
    @SendTo("/topic/chatroom/{senderId}/{receiverId}")
    public ChatMessage sendMessage(@DestinationVariable Long senderId, @DestinationVariable Long receiverId, @Payload ChatMessage chatMessage) {
        // 채팅방 생성 또는 가져오기
        System.out.println("Received message from sender " + senderId + " to receiver " + receiverId + ": " + chatMessage);
        ChatRoom chatRoom = chatService.createOrGetChatRoom(senderId, receiverId);
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setTimestamp(LocalDateTime.now().toString());

        // Redis에 메시지 저장
        chatService.saveMessageInRedis(chatMessage);

        // 구독자들에게 메시지 전송
        return chatMessage;
    }

    /**
     * 채팅방을 조회하는 메서드 (채팅방 클릭 시 호출)
     * @param senderId 메시지를 보낸 유저 ID
     * @param receiverId 메시지를 받은 유저 ID
     * @return 채팅방 정보
     */
    @GetMapping("/chatroom/{senderId}/{receiverId}")
    @ResponseBody
    public ResponseChat.ChatRoomDto getChatRoom(@PathVariable Long senderId, @PathVariable Long receiverId) {
        // 채팅방이 존재하면 반환, 없으면 생성
        ChatRoom chatRoom = chatService.getChatRoom(senderId, receiverId);
        if(chatRoom == null) {
            return null;
        }
        // ChatRoomDto로 변환하여 채팅방과 메시지 반환
        return new ResponseChat.ChatRoomDto(chatRoom);
    }

    @GetMapping("/get/chatRoom/{chatRoomId}")
    @ResponseBody
    public ResponseChat.ChatRoomDto getChatRoomId(@PathVariable String chatRoomId) {

        ChatRoom chatRoom = chatService.getChatRoomId(chatRoomId);
        // ChatRoomDto로 변환하여 채팅방과 메시지 반환
        return new ResponseChat.ChatRoomDto(chatRoom);
    }
    /**
     * 사용자가 참여 중인 채팅방 목록을 반환
     * @return 참여 중인 채팅방 목록
     */
    @GetMapping("/get/chatRooms")
    @ResponseBody
    public List<ResponseChat.ChatRoomListDto> getUserChatRooms(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseChat.ChatRoomListDto> response = chatService.getUserChatRooms(token);
        System.out.println(response);
        return response;
    }



//    /**
//     * 특정 채팅방에서 Redis에 저장된 메시지들을 조회
//     * @param chatRoomId 채팅방 ID
//     * @return 해당 채팅방의 메시지 목록
//     */
//    @GetMapping("/messages/{chatRoomId}")
//    public List<ChatMessage> getMessagesFromRedis(@PathVariable Long chatRoomId) {
//        // Redis에서 채팅 메시지 조회
//        return chatService.getMessagesFromRedis(chatRoomId);
//    }

    /**
     * 사용자가 채팅방을 떠날 때 호출되는 메서드
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = (String) headerAccessor.getSessionAttributes().get("chatRoomId");

        if (chatRoomId != null) {
            // 사용자가 채팅방을 떠났을 때 Redis의 메시지를 데이터베이스로 옮김
            chatService.persistMessagesToDatabase(chatRoomId);
        }
    }

}
