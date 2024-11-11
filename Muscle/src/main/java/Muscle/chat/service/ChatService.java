
package Muscle.chat.service;

import Muscle.auth.entity.Auth;
import Muscle.auth.entity.Follow;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.repository.FollowRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.chat.dto.ResponseChat;
import Muscle.chat.entity.ChatMessage;
import Muscle.chat.entity.ChatRoom;
import Muscle.chat.repository.ChatMessageRepository;
import Muscle.chat.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String CHAT_ROOM_PREFIX = "chatRoom:";
    private final RedisTemplate<String, Object> chatRedisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AuthRepository authRepository;
    private final FollowRepository followRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatRoom createOrGetChatRoom(Long senderId, Long receiverId) {

        Auth sender = authRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        Auth receiver = authRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 송신자와 수신자 간의 팔로우 관계 확인 (생략 가능)
        Follow sendToReceive = followRepository.findByFollowerAndFollowing(sender, receiver);
        Follow receiveToSender = followRepository.findByFollowerAndFollowing(receiver, sender);

        // 기존 채팅방이 있는지 확인 (양방향 검색)
        Optional<ChatRoom> existingRoom = chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .or(() -> chatRoomRepository.findBySenderIdAndReceiverId(receiverId, senderId));

        // 채팅방이 있으면 반환, 없으면 새로 생성
        return existingRoom.orElseGet(() -> {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setChatRoomId(UUID.randomUUID().toString());
            chatRoom.setRoomName(sender.getMuscleId()+receiver.getMuscleId());
            chatRoom.setSenderId(senderId);
            chatRoom.setReceiverId(receiverId);
            chatRoom.setCreatedAt(LocalDateTime.now().toString());
            return chatRoomRepository.save(chatRoom);
        });
    }

    @Transactional
    public ChatRoom getChatRoom(Long senderId, Long receiverId) {

        Auth sender = authRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Auth receiver = authRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 송신자와 수신자 간의 팔로우 관계 확인 (생략 가능)
        Follow sendToReceive = followRepository.findByFollowerAndFollowing(sender, receiver);
        Follow receiveToSender = followRepository.findByFollowerAndFollowing(receiver, sender);

        // 기존 채팅방이 있는지 확인 (양방향 검색)
        Optional<ChatRoom> existingRoom = chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .or(() -> chatRoomRepository.findBySenderIdAndReceiverId(receiverId, senderId));

        // Optional에서 ChatRoom으로 변환하여 반환, 없으면 null 반환
        return existingRoom.orElse(null);
    }


    @Transactional
    public ChatRoom getChatRoomId(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).get();
        return chatRoom;
    }

    public List<ResponseChat.ChatRoomListDto> getUserChatRooms(Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();

        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomsByUserId(userId);
        List<ResponseChat.ChatRoomListDto> dtoList = new ArrayList<>();
        chatRoomList.forEach(chatRoom -> {

            String senderImg = authRepository.findById(chatRoom.getSenderId()).get().getUserImg();
            String receiverImg = authRepository.findById(chatRoom.getSenderId()).get().getUserImg();
            dtoList.add(new ResponseChat.ChatRoomListDto(chatRoom, senderImg, receiverImg));
        });

        return dtoList;
    }

//    public List<ResponseChat.ChatRoomListDto> getUserChatRooms(Long userId) {
//        System.out.println("getUserChatRooms called with userId: " + userId); // 로그 추가
//
//        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomsByUserId(userId);
//        System.out.println("Chat rooms found: " + chatRoomList.size()); // 로그 추가
//
//        List<ResponseChat.ChatRoomListDto> dtoList = new ArrayList<>();
//        chatRoomList.forEach(chatRoom -> {
//            dtoList.add(new ResponseChat.ChatRoomListDto(chatRoom));
//        });
//
//        return dtoList;
//    }

    // Redis에 메시지 임시 저장
    public void saveMessageInRedis(ChatMessage chatMessage) {
        String key = CHAT_ROOM_PREFIX + chatMessage.getChatRoom().getChatRoomId();
        chatRedisTemplate.opsForList().rightPush(key, serializeMessage(chatMessage));
    }

    // 메시지 직렬화
    private String serializeMessage(ChatMessage message) {
        try {
            return new ObjectMapper().writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing message", e);
        }
    }

    // 메시지 역직렬화
    private ChatMessage deserializeMessage(Object message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 만약 Redis에서 가져온 데이터가 LinkedHashMap일 경우 처리
            if (message instanceof LinkedHashMap) {
                return objectMapper.convertValue(message, ChatMessage.class);
            }

            // 만약 Redis에서 가져온 데이터가 String일 경우 처리
            else if (message instanceof String) {
                return objectMapper.readValue((String) message, ChatMessage.class);
            } else {
                throw new IllegalArgumentException("Unsupported message format: " + message.getClass());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing message", e);
        }
    }


    public void persistMessagesToDatabase(String chatRoomId) {
        String key = CHAT_ROOM_PREFIX + chatRoomId;
        List<Object> serializedMessages = chatRedisTemplate.opsForList().range(key, 0, -1);

        // Redis에서 데이터를 ChatMessage로 변환 후 DB에 저장
        List<ChatMessage> messages = serializedMessages.stream()
                .map(this::deserializeMessage)  // 수정된 deserializeMessage 메서드 사용
                .collect(Collectors.toList());

        if (!messages.isEmpty()) {
            // ChatRoom을 찾음
            ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                    .orElseThrow(() -> new RuntimeException("ChatRoom not found with id: " + chatRoomId));

            // 각 메시지에 ChatRoom 설정
            messages.forEach(message -> message.setChatRoom(chatRoom));

            // 메시지 저장
            chatMessageRepository.saveAll(messages);

            // ChatRoom의 createdAt을 갱신
            chatRoom.setCreatedAt(LocalDateTime.now().toString());
            chatRoomRepository.save(chatRoom);

            // Redis에서 메시지 삭제
            chatRedisTemplate.delete(key);
        }
    }



    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    public void persistAllMessagesToDatabase() {
        // 모든 채팅방 조회
        List<ChatRoom> allChatRooms = chatRoomRepository.findAll();
        for (ChatRoom chatRoom : allChatRooms) {
            String redisKey = CHAT_ROOM_PREFIX + chatRoom.getChatRoomId();
            Long messageCount = chatRedisTemplate.opsForList().size(redisKey);

            // Redis에 메시지가 있는 경우만 처리
            if (messageCount != null && messageCount > 0) {
                persistMessagesToDatabase(chatRoom.getChatRoomId());
            }
        }
    }
}
