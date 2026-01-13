package edu.uoc.epcsd.productcatalog.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.productcatalog.application.rest.request.AvailabilityRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.BookingRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.LineRequest;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.domain.exception.BookingNotFoundException;
import edu.uoc.epcsd.productcatalog.domain.exception.ProductStockException;
import edu.uoc.epcsd.productcatalog.domain.mapper.BookingMapper;
import edu.uoc.epcsd.productcatalog.domain.repository.BookingRepository;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

  @Mock
  private BookingLineService bookingLineService;
  @Mock
  private ItemService itemService;
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private BookingMapper bookingMapper;
  @InjectMocks
  private BookingServiceImpl bookingServiceImpl;

  private LocalDate start;
  private LocalDate end;

  @BeforeEach
  void init() {
    start = LocalDate.now();
    end = start.plusDays(1);
  }

  @Test
  void checkAvailability_emptyProductIds_returnsEmptyMap() {
    AvailabilityRequest req = AvailabilityRequest.builder()
        .productIds(Set.of())
        .startDate(start)
        .endDate(end)
        .build();

    Map<Long, Integer> result = bookingServiceImpl.checkAvailability(req);

    assertThat(result).isEmpty();
  }

  @Test
  void checkAvailability_productWithoutStock_returnsZero() {
    Set<Long> ids = Set.of(1L);
    AvailabilityRequest req = AvailabilityRequest.builder()
        .productIds(ids)
        .startDate(start)
        .endDate(end)
        .build();

    when(itemService.countItemsByProductIdIn(anyCollection())).thenReturn(Map.of(1L, 0));
    when(bookingLineService.countReservedItemsPerProductAndDateRange(anyCollection(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Map.of());

    Map<Long, Integer> result = bookingServiceImpl.checkAvailability(req);

    assertThat(result).containsEntry(1L, 0);
  }

  @Test
  void checkAvailability_reservationsGreaterOrEqualStock_returnsZero() {
    Set<Long> ids = Set.of(2L);
    AvailabilityRequest req = AvailabilityRequest.builder()
        .productIds(ids)
        .startDate(start)
        .endDate(end)
        .build();

    when(itemService.countItemsByProductIdIn(anyCollection())).thenReturn(Map.of(2L, 3));
    when(bookingLineService.countReservedItemsPerProductAndDateRange(anyCollection(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Map.of(2L, 4));

    Map<Long, Integer> result = bookingServiceImpl.checkAvailability(req);

    assertThat(result).containsEntry(2L, 0);
  }

  @Test
  void checkAvailability_partialReservations_returnsRemaining() {
    Set<Long> ids = Set.of(3L, 4L);
    AvailabilityRequest req = AvailabilityRequest.builder()
        .productIds(ids)
        .startDate(start)
        .endDate(end)
        .build();

    when(itemService.countItemsByProductIdIn(anyCollection())).thenReturn(Map.of(3L, 5, 4L, 2));
    when(bookingLineService.countReservedItemsPerProductAndDateRange(anyCollection(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Map.of(3L, 2, 4L, 1));

    Map<Long, Integer> result = bookingServiceImpl.checkAvailability(req);

    assertThat(result).containsEntry(3L, 3).containsEntry(4L, 1);
  }

  @Test
  void createBooking_productsAvailable_savesBooking() {
    LineRequest line = LineRequest.builder().productId(1L).quantity(2).build();
    BookingRequest req = BookingRequest.builder()
        .startDate(start)
        .endDate(end)
        .lines(List.of(line))
        .build();

    when(itemService.countItemsByProductIdIn(anyCollection())).thenReturn(Map.of(1L, 5));
    when(bookingLineService.countReservedItemsPerProductAndDateRange(anyCollection(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Map.of(1L, 1));

    Booking booking = mock(Booking.class);
    CurrentUser user = mock(CurrentUser.class);
    when(bookingMapper.toDomain(any(BookingRequest.class), any(CurrentUser.class))).thenReturn(
        booking);
    when(bookingRepository.save(booking)).thenReturn(booking);

    Booking result = bookingServiceImpl.createBooking(req, user);

    assertThat(result).isSameAs(booking);
    verify(bookingMapper).toDomain(req, user);
    verify(bookingRepository).save(booking);
  }

  @Test
  void createBooking_insufficientStock_throwsProductStockException() {
    LineRequest line = LineRequest.builder().productId(1L).quantity(2).build();
    BookingRequest req = BookingRequest.builder()
        .startDate(start)
        .endDate(end)
        .lines(List.of(line))
        .build();

    when(itemService.countItemsByProductIdIn(anyCollection())).thenReturn(Map.of(1L, 1));
    when(bookingLineService.countReservedItemsPerProductAndDateRange(anyCollection(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Map.of(1L, 0));

    CurrentUser user = mock(CurrentUser.class);

    assertThrows(ProductStockException.class, () -> bookingServiceImpl.createBooking(req, user));
    verify(bookingMapper, never()).toDomain(any(BookingRequest.class), any(CurrentUser.class));
    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void givenBookingId_whenGetBookingByIdAndBookingDoesNotExist_thenThrowBookingNotFoundException() {
    long bookingId = 1L;
    CurrentUser currentUser = CurrentUser.builder()
        .id("42")
        .build();

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

    assertThrows(BookingNotFoundException.class,
        () -> bookingServiceImpl.getBookingById(bookingId, currentUser));
  }

  @Test
  void getBookingById_bookingExistsButDoesNotBelongToUser_throwsBookingNotFoundException() {
    long bookingId = 1L;
    Long userId = 42L;
    CurrentUser currentUser = CurrentUser.builder()
        .id("99") // Different user ID
        .build();

    Booking booking = mock(Booking.class);
    when(booking.getUserId()).thenReturn(userId);
    when(bookingRepository.findById(bookingId)).thenReturn(java.util.Optional.of(booking));

    assertThrows(BookingNotFoundException.class,
        () -> bookingServiceImpl.getBookingById(bookingId, currentUser));
  }

  @Test
  void getBookingById_bookingExistsAndBelongsToUser_returnsBooking() {
    long bookingId = 1L;
    Long userId = 42L;
    CurrentUser currentUser = CurrentUser.builder()
        .id(userId.toString())
        .build();

    Booking booking = mock(Booking.class);
    when(booking.getUserId()).thenReturn(userId);
    when(bookingRepository.findById(bookingId)).thenReturn(java.util.Optional.of(booking));

    Booking result = bookingServiceImpl.getBookingById(bookingId, currentUser);

    assertThat(result).isSameAs(booking);
  }

  @Test
  void givenCurrentUser_whenGetAllBookingsFromUser_thenReturnBookingsList() {
    Long userId = 42L;
    CurrentUser currentUser = CurrentUser.builder()
        .id(userId.toString())
        .build();

    Booking booking1 = mock(Booking.class);
    Booking booking2 = mock(Booking.class);
    List<Booking> bookings = List.of(booking1, booking2);

    when(bookingRepository.findByUserId(userId)).thenReturn(bookings);

    List<Booking> result = bookingServiceImpl.getAllBookingsFromUser(currentUser);

    assertThat(result).isEqualTo(bookings);
  }
}