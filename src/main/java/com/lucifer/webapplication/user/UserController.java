package com.lucifer.webapplication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @MessageMapping("/user.addUser")
  @SendTo("/user/public")
  public User addUser(
         @Payload User user
  ) {
    service.saveUser(user);
    return user;
  }

  @MessageMapping("/user.disconnectUser")
  @SendTo("/user/public")
  public User disconnect(
          @Payload User user
  ) {
    service.disconnect(user);
    return user;
  }

  @GetMapping(path = "/user")
  public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
    User foundUser = service.findUserByEmail(email);
    if (foundUser == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(foundUser);
  }

  /**
   * creates a user with email and name provided by client if user does not exist.
   * @return ResponseEntity with 201 and assigned userId.
   */
  @GetMapping(path = "/user/create")
  public ResponseEntity<User> createUser(@RequestParam("email") String email) {
    User foundUser = service.findUserByEmail(email);
    if (foundUser == null) {
      foundUser = service.createUser(email);
    }
    return ResponseEntity.ok(foundUser);
  }

  /**
   *
   * @return connected users that are part of chat with user
   */
  @GetMapping(path = "/users/{userId}")
  public ResponseEntity<List<User>> findConnectedUsers(@PathVariable("userId") String userId) {
    return ResponseEntity.ok(service.findChatUser(userId));
  }

}
