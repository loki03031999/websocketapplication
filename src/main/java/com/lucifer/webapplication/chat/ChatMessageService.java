package com.lucifer.webapplication.chat;

import com.lucifer.webapplication.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository repository;
  private final ChatRoomService chatRoomService;

  public ChatMessage save(ChatMessage chatMessage) {
    return repository.save(chatMessage);
  }

  public List<ChatMessage> findChatMessages(
          String chatRoomId
  ) {
    return repository.findByChatRoomId(chatRoomId);
  }

}
