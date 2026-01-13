package edu.uoc.epcsd.productcatalog.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProductNotFoundExceptionTest {

  @Test
  void productNotFound_constructsCorrectMessage() {
    ProductNotFoundException ex = new ProductNotFoundException(7L);
    assertEquals("Product with id '7' not found", ex.getMessage());
  }

}