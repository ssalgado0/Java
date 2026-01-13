package edu.uoc.epcsd.user.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotFoundExceptionTest {

  @Test
  void givenMessage_whenCreateException_thenMessageIsSet() {
    // given
    String message = "Resource not found";

    // when
    NotFoundException exception = new NotFoundException(message);

    // then
    assertNotNull(exception);
    assertEquals("Resource not found", exception.getMessage());
  }
}
