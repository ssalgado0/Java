package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.domain.booking.BookingLine;
import edu.uoc.epcsd.productcatalog.domain.booking.BookingStatus;
import edu.uoc.epcsd.productcatalog.domain.repository.BookingRepository;
import edu.uoc.epcsd.productcatalog.testutils.IntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BookingRepositoryImplIT extends IntegrationTest {

  @Autowired
  private BookingRepository bookingRepository;

  @Test
  void givenBooking_whenSave_thenBookingIsSaved() {
    Booking booking = Booking.builder()
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(5))
        .userId(1L)
        .status(BookingStatus.PENDING)
        .lines(List.of(
            BookingLine.builder()
                .product(Product.builder().id(1L).build())
                .quantity(3)
                .build()
        ))
        .build();

    Booking savedBooking = bookingRepository.save(booking);

    assertThat(savedBooking).isNotNull();
    assertThat(savedBooking.getId()).isNotNull();
    assertThat(savedBooking.getLines()).hasSize(1);
    assertThat(savedBooking).extracting("userId", "status", "startDate", "endDate")
        .containsExactly(1L, BookingStatus.PENDING, booking.getStartDate(), booking.getEndDate());

    BookingLine bookingLine = savedBooking.getLines().get(0);
    assertThat(bookingLine).isNotNull();
    assertThat(bookingLine.getId()).isNotNull();
    assertThat(bookingLine.getProduct()).isNotNull().extracting(Product::getId).isEqualTo(1L);
    assertThat(bookingLine.getQuantity()).isEqualTo(3);
  }

  @Test
  void givenInvalidId_whenFindById_thenEmptyOptionalIsReturned() {
    long invalidId = 9999L;

    var result = bookingRepository.findById(invalidId);

    assertThat(result).isEmpty();
  }

  @Test
  void givenValidId_whenFindById_thenBookingIsReturned() {
    long validId = 1L;

    var result = bookingRepository.findById(validId);

    assertThat(result).isPresent();
    Booking booking = result.get();
    assertThat(booking.getId()).isEqualTo(validId);
  }

  @Test
  void givenUserId_whenFindByUserId_thenBookingsAreReturned() {
    long userId = 5L;

    List<Booking> bookings = bookingRepository.findByUserId(userId);

    assertThat(bookings).isNotNull()
        .isNotEmpty()
        .allSatisfy(booking -> assertEquals(userId, booking.getUserId()));
  }

  @Test
  void givenUserIdWithNoBookings_whenFindByUserId_thenEmptyListIsReturned() {
    long userId = 9999L;

    List<Booking> bookings = bookingRepository.findByUserId(userId);

    assertThat(bookings).isNotNull().isEmpty();
  }
}