package org.example.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409
public class ReservationConflictException extends RuntimeException {
  public ReservationConflictException(String message) {
    super(message);
  }
}
