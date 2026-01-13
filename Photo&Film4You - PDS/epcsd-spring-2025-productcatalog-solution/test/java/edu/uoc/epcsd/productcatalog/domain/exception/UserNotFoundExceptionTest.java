package edu.uoc.epcsd.productcatalog.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {

  @Test
  void constructor_setsMessage() {
    String email = "john@example.com";
    UserNotFoundException ex = new UserNotFoundException(email);
    assertEquals("User with email 'john@example.com' not found", ex.getMessage());
  }

}