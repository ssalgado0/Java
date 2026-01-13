package edu.uoc.epcsd.user.infrastructure.security.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import edu.uoc.epcsd.user.domain.exception.UserNotFoundException;
import edu.uoc.epcsd.user.infrastructure.security.jwt.JwtValidationException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JwtValidationException.class)
  public ResponseEntity<ErrorResponse> handleJwtException(JwtValidationException ex) {
    log.warn("JWT validation error: {}", ex.getMessage());
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("Access denied"));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    log.warn("User not found: {}", ex.getMessage());
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
    log.warn("Response status exception: {}", ex.getMessage(), ex);
    return ResponseEntity
            .status(ex.getStatus())
            .body(new ErrorResponse(ex.getReason()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Internal server error"));
  }

  @Data
  @AllArgsConstructor
  public static class ErrorResponse {

    private String message;

    @JsonInclude(Include.NON_EMPTY)
    private List<String> validations;

    public ErrorResponse(String message) {
      this.message = message;
    }
  }
}
