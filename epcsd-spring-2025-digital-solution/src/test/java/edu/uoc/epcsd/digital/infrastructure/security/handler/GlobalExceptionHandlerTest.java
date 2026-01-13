package edu.uoc.epcsd.digital.infrastructure.security.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import edu.uoc.epcsd.digital.domain.exception.NotFoundException;
import edu.uoc.epcsd.digital.infrastructure.security.handler.GlobalExceptionHandler.ErrorResponse;
import edu.uoc.epcsd.digital.infrastructure.security.jwt.JwtValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

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
  void handleGenericException_shouldReturn500AndFixedMessage() {
    Exception ex = new Exception("boom");

    ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Internal server error", response.getBody().getMessage());
  }

  @Test
  void handleBadRequestException_shouldReturn400AndExceptionMessage() {
    IllegalArgumentException ex = new IllegalArgumentException("Bad request test");

    ResponseEntity<ErrorResponse> response = handler.handleBadRequestException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Bad request test", response.getBody().getMessage());
  }

  @Test
  void handleNotFoundException_shouldReturn404AndExceptionMessage() {
    NotFoundException ex = new NotFoundException("Not found test");

    ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Not found test", response.getBody().getMessage());
  }

  @Test
  void handleHttpMessageNotReadableException_shouldReturn400AndFixedMessage() {
    HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
    ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Malformed JSON request", response.getBody().getMessage());
  }
}