package edu.uoc.epcsd.productcatalog.domain.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface BookingLineService {

  Map<Long, Integer> countReservedItemsPerProductAndDateRange(
      Collection<Long> productIds, LocalDate startDate, LocalDate endDate);

  boolean existsAnyBookingForProduct(Long productId);

}
