package edu.uoc.epcsd.productcatalog.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

  @Test
  void badRequestException_messagePropagated() {
    BadRequestException ex = new BadRequestException("Bad input");
    assertEquals("Bad input", ex.getMessage());
  }

}