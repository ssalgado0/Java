package edu.uoc.epcsd.productcatalog.application.rest.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.uoc.epcsd.productcatalog.application.rest.request.AvailabilityRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateRangeValidatorTest {

  private final DateRangeValidator validator = new DateRangeValidator();

  @Test
  void givenNullValue_whenIsValid_thenReturnsTrue() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void givenNullStartDate_whenIsValid_thenReturnsTrue() {
    AvailabilityRequest request = new AvailabilityRequest();
    request.setStartDate(null);
    request.setEndDate(LocalDate.now());
    assertTrue(validator.isValid(request, null));
  }

  @Test
  void givenNullEndDate_whenIsValid_thenReturnsTrue() {
    AvailabilityRequest request = new AvailabilityRequest();
    request.setStartDate(LocalDate.now());
    request.setEndDate(null);
    assertTrue(validator.isValid(request, null));
  }

  @Test
  void givenStartDateBeforeEndDate_whenIsValid_thenReturnsTrue() {
    AvailabilityRequest request = new AvailabilityRequest();
    request.setStartDate(LocalDate.of(2024, 1, 1));
    request.setEndDate(LocalDate.of(2024, 1, 2));
    assertTrue(validator.isValid(request, null));
  }

  @Test
  void givenStartDateEqualToEndDate_whenIsValid_thenReturnsFalse() {
    AvailabilityRequest request = new AvailabilityRequest();
    request.setStartDate(LocalDate.of(2024, 1, 1));
    request.setEndDate(LocalDate.of(2024, 1, 1));
    assertFalse(validator.isValid(request, null));
  }

  @Test
  void givenStartDateAfterEndDate_whenIsValid_thenReturnsFalse() {
    AvailabilityRequest request = new AvailabilityRequest();
    request.setStartDate(LocalDate.of(2024, 1, 2));
    request.setEndDate(LocalDate.of(2024, 1, 1));
    assertFalse(validator.isValid(request, null));
  }
}