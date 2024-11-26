package com.lucifer.webapplication.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
  public List<User> findAllByStatus(Status status);
  public Optional<User> findByEmail(String email);
}
