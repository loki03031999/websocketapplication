package com.lucifer.webapplication.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class BaseError extends RuntimeException {
  protected String reason;
}
