package edu.uoc.epcsd.user.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AlertNotFoundExceptionTest {

  @Test
  void givenAlertId_whenCreateException_thenMessageContainsId() {
    // given
    Long alertId = 123L;

    // when
    AlertNotFoundException exception = new AlertNotFoundException(alertId);

    // then
    assertNotNull(exception);
    assertEquals("The specified Alert id 123 does not exist.", exception.getMessage());
  }

  @Test
  void givenAlertIdAndEmail_whenCreateException_thenMessageContainsBoth() {
    // given
    Long alertId = 456L;
    String email = "test@example.com";

    // when
    AlertNotFoundException exception = new AlertNotFoundException(alertId, email);

    // then
    assertNotNull(exception);
    assertEquals("The specified Id or AlertId 456 / test@example.com does not exist.", exception.getMessage());
  }
}
