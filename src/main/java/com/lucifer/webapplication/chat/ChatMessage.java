package com.lucifer.webapplication.chat;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatMessage {
  @Id
  private String id;
  private String chatRoomId;
  private String senderId;
  private String recipientId;
  private String message;
  private Date timestamp;
}
