package com.lucifer.webapplication.chatroom;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {
  @Id
  private String id;
  private List<String> participantIds;

  @CreatedDate
  private Date created;
  private Date lastUpdated;
}
