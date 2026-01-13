package edu.uoc.epcsd.digital.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProductNotFoundExceptionTest {

  @Test
  void constructor_ShouldSetCorrectMessage() {
    Long productId = 123L;
    ProductNotFoundException exception = new ProductNotFoundException(productId);

    assertEquals("Product with id '" + productId + "' not found", exception.getMessage());
  }
}
