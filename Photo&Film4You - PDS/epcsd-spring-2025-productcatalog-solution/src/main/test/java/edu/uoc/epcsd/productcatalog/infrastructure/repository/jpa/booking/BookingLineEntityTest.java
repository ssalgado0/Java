package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BookingLineEntityTest {

  @Test
  void testGetTotalPrice() {
    BookingLineEntity bookingLine = BookingLineEntity.builder()
        .pricePerUnit(new BigDecimal(100))
        .quantity(3)
        .build();

    BigDecimal expectedTotalPrice = new BigDecimal(300);
    assertEquals(expectedTotalPrice, bookingLine.getTotalPrice());
  }


}