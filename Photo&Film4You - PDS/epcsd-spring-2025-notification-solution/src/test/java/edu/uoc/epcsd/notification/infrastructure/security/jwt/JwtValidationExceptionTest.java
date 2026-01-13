package edu.uoc.epcsd.notification.infrastructure.security.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtValidationExceptionTest {

  @Test
  void givenMessage_whenCreateException_thenMessageIsSet() {
    // given
    String message = "JWT token expired";

    // when
    JwtValidationException exception = new JwtValidationException(message);

    // then
    assertNotNull(exception);
    assertEquals("JWT token expired", exception.getMessage());
  }

  @Test
  void givenMessageAndCause_whenCreateException_thenBothAreSet() {
    // given
    String message = "Invalid JWT token";
    Throwable cause = new RuntimeException("Original error");

    // when
    JwtValidationException exception = new JwtValidationException(message, cause);

    // then
    assertNotNull(exception);
    assertEquals("Invalid JWT token", exception.getMessage());
    assertEquals("Original error", exception.getCause().getMessage());
  }
}
