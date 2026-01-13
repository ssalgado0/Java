package edu.uoc.epcsd.productcatalog.application.rest.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {

  String message() default "startDate must be before endDate";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
