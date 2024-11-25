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
    var chatId = chatRoomService.getChatRoomId(
            chatMessage.getSenderId(),
            chatMessage.getRecipientId(),
            true
    ).orElseThrow();//create your own dedicated exception
    chatMessage.setChatId(chatId);
    repository.save(chatMessage);
    return chatMessage;
  }

  public List<ChatMessage> findChatMessages(
          String senderId,
          String recipientId
  ) {
    var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
    return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
  }

}
