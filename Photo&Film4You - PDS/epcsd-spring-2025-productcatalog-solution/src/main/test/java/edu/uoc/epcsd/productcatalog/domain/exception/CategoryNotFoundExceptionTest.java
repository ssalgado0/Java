package edu.uoc.epcsd.productcatalog.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CategoryNotFoundExceptionTest {

  @Test
  void categoryNotFound_constructsCorrectMessage() {
    CategoryNotFoundException ex = new CategoryNotFoundException(42L);
    assertEquals("Category with id '42' not found", ex.getMessage());
  }

}