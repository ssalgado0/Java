package edu.uoc.epcsd.digital.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DigitalSessionNotFoundExceptionTest {

  @Test
  void constructor_WithId_ShouldSetCorrectMessage() {
    Long digitalSessionId = 789L;
    DigitalSessionNotFoundException exception = new DigitalSessionNotFoundException(
        digitalSessionId);

    assertEquals("The specified DigitalSession id " + digitalSessionId + " does not exist.",
        exception.getMessage());
  }

  @Test
  void constructor_WithIdAndEmail_ShouldSetCorrectMessage() {
    Long digitalSessionId = 789L;
    String email = "user@example.com";
    DigitalSessionNotFoundException exception = new DigitalSessionNotFoundException(
        digitalSessionId, email);

    assertEquals(
        "The specified Id or UserId " + digitalSessionId + " / " + email + " does not exist.",
        exception.getMessage());
  }
}
