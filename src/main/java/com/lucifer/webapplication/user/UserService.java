package com.lucifer.webapplication.user;

import com.lucifer.webapplication.chatroom.ChatRoom;
import com.lucifer.webapplication.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository repository;
  private final ChatRoomService chatRoomService;

  public void saveUser(User user) {
    user.setStatus(Status.ONLINE);
    repository.save(user);
  }

  public void disconnect(User user) {
    var storedUser = repository.findById(user.getId()).orElse(null);
    if (storedUser != null) {
      storedUser.setStatus(Status.OFFLINE);
      repository.save(storedUser);
    }
  }

  public User findUserByEmail(String emailId) {
    return repository.findByEmail(emailId).orElse(null);
  }

  public List<User> findChatUser(String userId) {
    User user = repository.findById(userId).orElse(null);
    if (user == null) return Collections.emptyList();

    List<ChatRoom> chatRooms = chatRoomService.getChatRoomByIds(user.getChatRoomIds());

    List<String> participantIds = new ArrayList<>();

    for (ChatRoom chatRoom : chatRooms) {
      for (String participantId : chatRoom.getParticipantIds()) {
        if (!participantId.equals(userId)) participantIds.add(participantId);
      }
    }
    return repository.findAllById(participantIds);
  }

  public User createUser(String email) {

    User user = User.builder()
            .name(email)
            .email(email)
            .chatRoomIds(Collections.emptyList())
            .build();

    user = repository.save(user);
    return user;
  }
}
