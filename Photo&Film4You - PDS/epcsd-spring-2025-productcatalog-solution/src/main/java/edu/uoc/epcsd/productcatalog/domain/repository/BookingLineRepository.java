package edu.uoc.epcsd.productcatalog.domain.repository;

import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ItemsPerProductCount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BookingLineRepository {

  List<ItemsPerProductCount> countReservedItemsPerProductAndDateRange(Collection<Long> productIds,
      LocalDate startDate, LocalDate endDate);

  boolean existsAnyBookingForProduct(Long productId);
}
