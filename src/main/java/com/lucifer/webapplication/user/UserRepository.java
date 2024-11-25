package com.lucifer.webapplication.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
  public List<User> findAllByStatus(Status status);
}
