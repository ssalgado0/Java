package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import static org.assertj.core.api.Assertions.assertThat;

import edu.uoc.epcsd.productcatalog.domain.repository.BookingLineRepository;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ItemsPerProductCount;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ProductEntity;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.SpringDataProductRepository;
import edu.uoc.epcsd.productcatalog.testutils.IntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BookingLineRepositoryImplIT extends IntegrationTest {

  private final LocalDate startDate = LocalDate.of(2025, 1, 1);
  private final LocalDate endDate = LocalDate.of(2025, 1, 10);
  @Autowired
  private SpringDataBookingRepository springDataBookingRepository;
  @Autowired
  private SpringDataBookingLineRepository springDataBookingLineRepository;
  @Autowired
  private SpringDataProductRepository springDataProductRepository;
  @Autowired
  private BookingLineRepository bookingLineRepository;
  private BookingEntity booking;
  private BookingLineEntity bookingLine1;
  private BookingLineEntity bookingLine2;
  private ProductEntity product1;
  private ProductEntity product2;

  @BeforeEach
  void setUp() {
    product1 = springDataProductRepository.getById(1L);
    product2 = springDataProductRepository.getById(2L);

    booking = springDataBookingRepository.saveAndFlush(BookingEntity.builder()
        .startDate(startDate)
        .endDate(endDate)
        .status(BookingStatus.PENDING)
        .userId(1L)
        .build());

    bookingLine1 = springDataBookingLineRepository.saveAndFlush(BookingLineEntity.builder()
        .booking(booking)
        .product(product1)
        .quantity(2)
        .pricePerUnit(new BigDecimal(2))
        .build());
    bookingLine2 = springDataBookingLineRepository.saveAndFlush(BookingLineEntity.builder()
        .booking(booking)
        .product(product2)
        .quantity(3)
        .pricePerUnit(BigDecimal.ONE)
        .build());
  }

  @AfterEach
  void tearDown() {
    springDataBookingLineRepository.deleteAll(List.of(bookingLine1, bookingLine2));
    springDataBookingRepository.delete(booking);
  }

  @Test
  void givenNonExistingProductIds_whenCountReservedItemsPerProductAndDateRange_thenReturnEmptyList() {
    Long productId = 999L;

    List<ItemsPerProductCount> result = bookingLineRepository
        .countReservedItemsPerProductAndDateRange(List.of(productId), startDate, endDate);

    assertThat(result).isEmpty();
  }

  @Test
  void givenExistingProductIds_whenCountReservedItemsPerProductAndDateRange_thenReturnCounts() {
    Long productId1 = product1.getId();
    Long productId2 = product2.getId();

    List<ItemsPerProductCount> result = bookingLineRepository
        .countReservedItemsPerProductAndDateRange(List.of(productId1, productId2),
            startDate.minusDays(10),
            endDate.plusDays(10));

    assertThat(result).hasSize(2)
        .anySatisfy(count -> {
          assertThat(count.getProductId()).isEqualTo(productId1);
          assertThat(count.getQuantity()).isEqualTo(2);
        })
        .anySatisfy(count -> {
          assertThat(count.getProductId()).isEqualTo(productId2);
          assertThat(count.getQuantity()).isEqualTo(3);
        });
  }

  @Test
  void givenExistingProductIds_whenCountReservedItemsPerProductAndDateRangeWithNoOverlappingBookings_thenReturnEmptyList() {
    Long productId1 = product1.getId();
    Long productId2 = product2.getId();

    List<ItemsPerProductCount> result = bookingLineRepository
        .countReservedItemsPerProductAndDateRange(List.of(productId1, productId2),
            endDate.plusDays(1),
            endDate.plusDays(10));

    assertThat(result).isEmpty();
  }
}