package com.lucifer.webapplication.errors;

import lombok.*;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
  private HttpStatusCode status;
  private String message;
}
