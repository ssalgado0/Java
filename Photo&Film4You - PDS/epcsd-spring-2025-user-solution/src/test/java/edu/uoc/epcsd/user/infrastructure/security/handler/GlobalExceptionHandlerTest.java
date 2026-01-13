package edu.uoc.epcsd.user.infrastructure.security.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.uoc.epcsd.user.infrastructure.security.handler.GlobalExceptionHandler.ErrorResponse;
import edu.uoc.epcsd.user.infrastructure.security.jwt.JwtValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleJwtException_shouldReturn401AndExceptionMessage() {
    JwtValidationException ex = new JwtValidationException("Invalid token test");

    ResponseEntity<ErrorResponse> response = handler.handleJwtException(ex);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Invalid token test", response.getBody().getMessage());
  }

  @Test
  void handleAccessDeniedException_shouldReturn403AndFixedMessage() {
    AccessDeniedException ex = new AccessDeniedException("no access");

    ResponseEntity<ErrorResponse> response = handler.handleAccessDeniedException(ex);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Access denied", response.getBody().getMessage());
  }

  @Test
  void handleResponseStatusException_shouldReturnStatusAndMessage() {
    ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

    ResponseEntity<ErrorResponse> response = handler.handleResponseStatusException(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("not found", response.getBody().getMessage());
  }

  @Test
  void handleGenericException_shouldReturn500AndFixedMessage() {
    Exception ex = new Exception("boom");

    ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Internal server error", response.getBody().getMessage());
  }
}