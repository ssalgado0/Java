package edu.uoc.epcsd.productcatalog.domain.service;

import edu.uoc.epcsd.productcatalog.domain.repository.BookingLineRepository;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ItemsPerProductCount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingLineServiceImpl implements BookingLineService {

  private final BookingLineRepository bookingLineRepository;

  @Override
  public Map<Long, Integer> countReservedItemsPerProductAndDateRange(Collection<Long> productIds,
      LocalDate startDate, LocalDate endDate) {
    if (CollectionUtils.isEmpty(productIds)) {
      log.warn("No product IDs provided for counting items");
      throw new IllegalArgumentException("No product IDs provided for counting items");
    }
    return bookingLineRepository.countReservedItemsPerProductAndDateRange(productIds, startDate,
            endDate)
        .stream()
        .collect(Collectors.toMap(
            ItemsPerProductCount::getProductId,
            ItemsPerProductCount::getQuantity));
  }

  @Override
  public boolean existsAnyBookingForProduct(Long productId) {
    return bookingLineRepository.existsAnyBookingForProduct(productId);
  }
}
