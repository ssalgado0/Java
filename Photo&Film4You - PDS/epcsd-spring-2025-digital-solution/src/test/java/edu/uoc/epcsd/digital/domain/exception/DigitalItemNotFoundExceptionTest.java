package edu.uoc.epcsd.digital.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DigitalItemNotFoundExceptionTest {

  @Test
  void constructor_ShouldSetCorrectMessage() {
    Long digitalItemId = 456L;
    DigitalItemNotFoundException exception = new DigitalItemNotFoundException(digitalItemId);

    assertEquals("The specified DigitalItem id " + digitalItemId + " does not exist.",
        exception.getMessage());
  }
}
