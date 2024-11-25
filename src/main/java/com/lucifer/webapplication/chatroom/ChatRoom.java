package com.lucifer.webapplication.chatroom;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {

  @Id
  private String id;
  private String chatId;
  private String senderId;
  private String recipientId;

}
