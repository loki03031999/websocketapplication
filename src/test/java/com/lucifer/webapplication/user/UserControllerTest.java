package com.lucifer.webapplication.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class UserControllerTest {
  public static final String RANDOM_EMAIL = "randomemail@gmail.com";
  public static final String VALID_NAME = "Valid Name";
  public static final String VALID_EMAIL = "validemail@email.com";
  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  MongoTemplate mongoTemplate;

  private static String URL;
  private static final String GET_USER_BY_EMAIL_TEMPLATE = "/user?email={email}";
  private static final String EMAIL = "email";
  private static final String CREATE_USER_TEMPLATE = "/user/create?email={email}";

  @BeforeEach
  void setUp() {
    URL = "http://localhost:" + port;
  }

  @AfterEach
  void releaseResource() {
    mongoTemplate.dropCollection("user");
  }

  @Test
  void getUserByEmailEmptyEmail() {
    ResponseEntity<Object> response = restTemplate.getForEntity(getEndpoint(GET_USER_BY_EMAIL_TEMPLATE), Object.class, createQueryParam(""));
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void getUserByEmailUserNotPresent() {
    ResponseEntity<Object> response = restTemplate.getForEntity(getEndpoint(GET_USER_BY_EMAIL_TEMPLATE), Object.class, createQueryParam(RANDOM_EMAIL));
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void getUserByEmailUserPresent() {
    createValidUser();
    ResponseEntity<User> response = restTemplate.getForEntity(getEndpoint(GET_USER_BY_EMAIL_TEMPLATE), User.class, createQueryParam(VALID_EMAIL));
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());

    Assertions.assertEquals(response.getBody().getEmail(), VALID_EMAIL);
    Assertions.assertEquals(response.getBody().getName(), VALID_NAME);
  }

  @Test
  void createUserInvalidEmail() {
    ResponseEntity<Object> response = restTemplate.getForEntity(getEndpoint(CREATE_USER_TEMPLATE), Object.class, createQueryParam(""));
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void createUserUserDoesNotExist() {
    ResponseEntity<User> response = restTemplate.getForEntity(getEndpoint(CREATE_USER_TEMPLATE), User.class, createQueryParam(VALID_EMAIL));
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());

    Assertions.assertEquals(response.getBody().getEmail(), VALID_EMAIL);
    Assertions.assertEquals(response.getBody().getName(), VALID_EMAIL);
  }

  @Test
  void createUserAlreadyExist() {
    createValidUser();
    ResponseEntity<User> response = restTemplate.getForEntity(getEndpoint(CREATE_USER_TEMPLATE), User.class, createQueryParam(VALID_EMAIL));
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());

    Assertions.assertEquals(response.getBody().getEmail(), VALID_EMAIL);
    Assertions.assertEquals(response.getBody().getName(), VALID_NAME);
  }

  @Test
  void findConnectedUsers() {
    createValidUser();
  }

  private String getEndpoint(String path) {
    return URL + path;
  }

  private Map<String, String> createQueryParam(String email) {
    return Collections.singletonMap(EMAIL, email);
  }

  private void createValidUser() {
    mongoTemplate.save(User.builder()
            .name(VALID_NAME)
            .email(VALID_EMAIL)
            .status(Status.OFFLINE)
            .build());
  }
}