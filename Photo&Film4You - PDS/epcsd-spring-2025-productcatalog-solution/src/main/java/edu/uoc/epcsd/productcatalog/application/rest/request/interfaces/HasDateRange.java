package edu.uoc.epcsd.productcatalog.application.rest.request.interfaces;

import edu.uoc.epcsd.productcatalog.application.rest.validation.ValidDateRange;
import java.time.LocalDate;

@ValidDateRange
public interface HasDateRange {

  LocalDate getStartDate();

  LocalDate getEndDate();
}
