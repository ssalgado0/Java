package edu.uoc.epcsd.productcatalog.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

  @Test
  void notFoundException_messagePropagated() {
    NotFoundException ex = new NotFoundException("Not found");
    assertEquals("Not found", ex.getMessage());
  }

}