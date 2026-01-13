package edu.uoc.epcsd.productcatalog.application.rest.validation;

import static org.apache.commons.lang3.ObjectUtils.anyNull;

import edu.uoc.epcsd.productcatalog.application.rest.request.interfaces.HasDateRange;
import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, HasDateRange> {

  @Override
  public boolean isValid(HasDateRange value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    LocalDate start = value.getStartDate();
    LocalDate end = value.getEndDate();

    if (anyNull(start, end)) {
      return true;
    }

    return start.isBefore(end);
  }
}
