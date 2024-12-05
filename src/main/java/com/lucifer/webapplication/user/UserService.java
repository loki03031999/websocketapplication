package com.lucifer.webapplication.user;

import com.lucifer.webapplication.chatroom.ChatRoom;
import com.lucifer.webapplication.chatroom.ChatRoomService;
import com.lucifer.webapplication.errors.InvalidEmail;
import com.lucifer.webapplication.errors.InvalidUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
  Logger logger = LoggerFactory.getLogger(UserService.class);
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
    emailId = StringUtils.trim(emailId);
    validateEmail(emailId);
    User user = repository.findByEmail(emailId).orElse(null);

    if (user == null) {
      throw new InvalidUser("User does not exist");
    }
    return user;
  }

  public User createUser(String email) {
    validateEmail(email);
    User user = User.builder()
            .name(email)
            .email(email)
            .chatRoomIds(Collections.emptyList())
            .build();
    return repository.save(user);
  }

  public List<User> findChatUser(String userId) {
    User user = repository.findById(userId).orElse(null);
    if (user == null) {
      throw new InvalidUser("user does not exist");
    }

    List<ChatRoom> chatRooms = chatRoomService.getChatRoomByIds(user.getChatRoomIds());

    List<String> participantIds = new ArrayList<>();

    for (ChatRoom chatRoom : chatRooms) {
      for (String participantId : chatRoom.getParticipantIds()) {
        if (!participantId.equals(userId)) participantIds.add(participantId);
      }
    }
    return repository.findAllById(participantIds);
  }

  private void validateEmail(String email) {
    if (!EmailValidator.getInstance().isValid(email)) {
      logger.error("Email validation failed, Reason - provided email is invalid");
      throw new InvalidEmail("Invalid Email");
    }
  }
}
