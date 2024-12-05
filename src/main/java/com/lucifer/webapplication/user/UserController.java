package com.lucifer.webapplication.user;

import com.lucifer.webapplication.errors.ErrorResponse;
import com.lucifer.webapplication.errors.InvalidEmail;
import com.lucifer.webapplication.errors.InvalidUser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
  private final Logger logger = LoggerFactory.getLogger(UserController.class);
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
    return ResponseEntity.ok(foundUser);
  }

  /**
   * creates a user with email and name.
   * @return ResponseEntity with 201 and assigned userId.
   */
  @GetMapping(path = "/user/create")
  public ResponseEntity<User> createUser(@RequestParam("email") String email) {
    User foundUser = null;
    try {
      foundUser = service.findUserByEmail(email);
    }
    catch (InvalidUser invalidUserException) {
      logger.info("User does not exist, Creating new user");
    }
    catch (Exception exception) {
      logger.warn("error occured during user lookup");
      throw exception;
    }

    if (foundUser != null) {
      logger.info("user already exists");
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(foundUser);
    }

    foundUser = service.createUser(email);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(foundUser);
  }

  /**
   *
   * @return connected users that are part of chat with user
   */
  @GetMapping(path = "/users/{userId}")
  public ResponseEntity<List<User>> findConnectedUsers(@PathVariable("userId") String userId) {
    return ResponseEntity.ok(service.findChatUser(userId));
  }

  @ExceptionHandler(value = {InvalidUser.class, InvalidEmail.class, Exception.class})
  private ResponseEntity<ErrorResponse> handleExceptions(Exception exception) {
    if (exception instanceof InvalidEmail) {
      return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Invalid Email");
    }
    else if (exception instanceof InvalidUser) {
      return buildErrorResponseEntity(HttpStatus.NOT_FOUND, "User does not exist");
    }
    else {
      return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
  }

  private ResponseEntity<ErrorResponse> buildErrorResponseEntity(HttpStatus status, String message) {
    return ResponseEntity
            .status(status)
            .body(ErrorResponse.builder()
                    .status(status)
                    .message(message)
                    .build());
  }
}
