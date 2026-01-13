package edu.uoc.epcsd.productcatalog.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.productcatalog.domain.repository.BookingLineRepository;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ItemsPerProductCount;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingLineServiceImplTest {

  private final LocalDate date = LocalDate.of(2024, 1, 1);
  @Mock
  private BookingLineRepository bookingLineRepository;
  @InjectMocks
  private BookingLineServiceImpl bookingLineServiceImpl;

  @ParameterizedTest
  @NullAndEmptySource
  void givenEmptyCollection_whenCountItemsByProductIdIn_thenThrowsException(List<Long> productIds) {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> bookingLineServiceImpl.countReservedItemsPerProductAndDateRange(productIds, date,
            date));

    assertThat(ex.getMessage()).isEqualTo("No product IDs provided for counting items");
  }

  @Test
  void givenValidCollection_whenCountItemsByProductIdInAndNoResults_thenReturnEmptyMap() {
    when(bookingLineRepository.countReservedItemsPerProductAndDateRange(anyCollection(), any(),
        any()))
        .thenReturn(Collections.emptyList());

    var result = bookingLineServiceImpl.countReservedItemsPerProductAndDateRange(
        List.of(1L, 2L, 3L), date, date);

    assertThat(result).isEmpty();
  }

  @Test
  void givenValidCollection_whenCountItemsByProductIdInAndResults_thenReturnMapWithCounts() {
    when(bookingLineRepository.countReservedItemsPerProductAndDateRange(anyCollection(), any(),
        any()))
        .thenReturn(List.of(
            new ItemsPerProductCountImpl(1L, 5),
            new ItemsPerProductCountImpl(2L, 10)
        ));

    var result = bookingLineServiceImpl.countReservedItemsPerProductAndDateRange(
        List.of(1L, 2L, 3L), date, date);

    assertThat(result).hasSize(2)
        .containsEntry(1L, 5)
        .containsEntry(2L, 10)
        .doesNotContainKey(3L);
  }

  @Data
  @AllArgsConstructor
  static class ItemsPerProductCountImpl implements ItemsPerProductCount {

    private Long productId;
    private Integer quantity;
  }

}