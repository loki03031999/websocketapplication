package com.lucifer.webapplication.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document
@Builder
public class User {

  @Id
  private String id;
  private String name;
  @Indexed(unique = true)
  private String email;
  private Status status;
  private Date lastSeen;
  //chatroom that the user is part of, also included one on one chats
  private List<String> chatRoomIds;

  @CreatedDate
  private Date created;
  private Date lastUpdated;
}
