package edu.uoc.epcsd.digital.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

  @Test
  void constructor_ShouldSetMessage() {
    String message = "Resource not found";
    NotFoundException exception = new NotFoundException(message);

    assertEquals(message, exception.getMessage());
  }
}
