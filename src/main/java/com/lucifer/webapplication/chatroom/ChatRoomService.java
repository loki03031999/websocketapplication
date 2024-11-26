package com.lucifer.webapplication.chatroom;

import com.lucifer.webapplication.user.User;
import com.lucifer.webapplication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;

  public List<ChatRoom> getChatRoomByIds(List<String> chatRoomIds) {
    return chatRoomRepository.findAllById(chatRoomIds);
  }

  public ChatRoom createChatRoom(String senderId, String recipientId) {
    ChatRoom chatRoom = ChatRoom.builder()
            .participantIds(Collections.emptyList())
            .build();

    chatRoom.getParticipantIds().add(senderId);
    chatRoom.getParticipantIds().add(recipientId);

    User sender = userRepository.findById(senderId).orElse(null);
    User receiver = userRepository.findById(recipientId).orElse(null);
    if (sender == null || receiver == null) return null;

    chatRoom = chatRoomRepository.save(chatRoom);

    if (sender.getChatRoomIds() == null) {
      sender.setChatRoomIds(Collections.emptyList());
    }

    if (receiver.getChatRoomIds() == null) {
      receiver.setChatRoomIds(Collections.emptyList());
    }

    sender.getChatRoomIds().add(chatRoom.getId());
    receiver.getChatRoomIds().add(chatRoom.getId());

    userRepository.save(sender);
    userRepository.save(receiver);

    return chatRoom;
  }

}
