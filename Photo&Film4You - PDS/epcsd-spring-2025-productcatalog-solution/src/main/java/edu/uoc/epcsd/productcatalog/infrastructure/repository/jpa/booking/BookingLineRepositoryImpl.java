package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import edu.uoc.epcsd.productcatalog.domain.repository.BookingLineRepository;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ItemsPerProductCount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingLineRepositoryImpl implements BookingLineRepository {

  private final SpringDataBookingLineRepository jpaRepository;

  @Override
  public List<ItemsPerProductCount> countReservedItemsPerProductAndDateRange(
      Collection<Long> productIds, LocalDate startDate, LocalDate endDate) {
    return jpaRepository.countReservedItemsPerProductAndDateRange(productIds, startDate, endDate);
  }

  @Override
  public boolean existsAnyBookingForProduct(Long productId) {
    return jpaRepository.existsAnyBookingForProduct(productId);
  }
}
