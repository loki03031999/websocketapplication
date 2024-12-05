package com.lucifer.webapplication.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InvalidEmail extends RuntimeException {
  private String reason;
}
