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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class UserControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  MongoTemplate mongoTemplate;

  private String url;

  @BeforeEach
  void setUp() {
    url = "http://localhost:" + port;
  }

  @AfterEach
  void releaseResource() {
    mongoTemplate.dropCollection("user");
  }

  @Test
  void getUserByEmail() {
    ResponseEntity<Object> user = restTemplate.getForEntity(url + "/user", Object.class);
    Assertions.assertEquals(HttpStatus.NOT_FOUND, user.getStatusCode());
    Assertions.assertNotNull(user);
  }

  @Test
  void createUser() {
    restTemplate.getForEntity(url + "/user/create?email=" + "randomemail.com", Object.class);
    ResponseEntity<User> user = restTemplate.getForEntity(url + "/user?email=" + "randomemail.com", User.class);
    Assertions.assertEquals(user.getStatusCode(), HttpStatus.OK);
    Assertions.assertNotNull(user);
  }

  @Test
  void findConnectedUsers() {
  }
}