package edu.uoc.epcsd.digital.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DomainExceptionTest {

  @Test
  void constructor_ShouldSetMessage() {
    String message = "Domain error occurred";
    DomainException exception = new DomainException(message);

    assertEquals(message, exception.getMessage());
  }
}
