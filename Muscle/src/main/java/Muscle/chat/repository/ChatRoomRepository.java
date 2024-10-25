package Muscle.chat.repository;

import Muscle.chat.entity.ChatMessage;
import Muscle.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findBySenderIdAndReceiverId(Long senderId, Long receiveId);
    Optional<ChatRoom> findByReceiverIdAndSenderId(Long receiveId, Long senderId);// 유저가 송신자이거나 수신자인 채팅방 목록을 조회
    List<ChatRoom> findBySenderIdOrReceiverId(Long senderId, Long receiverId);


}
