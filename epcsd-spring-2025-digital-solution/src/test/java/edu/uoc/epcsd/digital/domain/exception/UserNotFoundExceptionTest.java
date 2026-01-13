package edu.uoc.epcsd.digital.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {

  @Test
  void constructor_ShouldSetCorrectMessage() {
    String email = "test@example.com";
    UserNotFoundException exception = new UserNotFoundException(email);

    assertEquals("User with id '" + email + "' not found", exception.getMessage());
  }
}
