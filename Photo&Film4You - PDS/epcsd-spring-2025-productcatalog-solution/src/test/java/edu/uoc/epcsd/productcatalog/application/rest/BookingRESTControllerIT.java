package edu.uoc.epcsd.productcatalog.application.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.uoc.epcsd.productcatalog.application.rest.request.AvailabilityRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.BookingRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.LineRequest;
import edu.uoc.epcsd.productcatalog.application.rest.response.AvailabilityResponse;
import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.domain.booking.BookingLine;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import edu.uoc.epcsd.productcatalog.testutils.IntegrationTest;
import edu.uoc.epcsd.productcatalog.testutils.TestJwtFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class BookingRESTControllerIT extends IntegrationTest {

  private final String availabilityUrl = "/bookings/availability";
  private final String bookingsUrl = "/bookings";
  private final String bookingsIdUrl = "/bookings/{id}";

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private TestJwtFactory jwtFactory;

  private CurrentUser currentUser;

  @BeforeEach
  void setUp() {
    currentUser = CurrentUser.builder()
        .id("1")
        .email("test@test.com")
        .fullName("Test User")
        .role("USER")
        .build();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void givenEmptyProductIds_whenCheckAvailability_thenBadRequest(Set<Long> productIds) {
    AvailabilityRequest request = AvailabilityRequest.builder()
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(1))
        .productIds(productIds)
        .build();

    ResponseEntity<AvailabilityResponse> response = restTemplate.postForEntity(
        baseUrl() + availabilityUrl,
        request,
        AvailabilityResponse.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void givenPastDates_whenCheckAvailability_thenBadRequest() {
    AvailabilityRequest request = AvailabilityRequest.builder()
        .startDate(LocalDate.now().minusDays(5))
        .endDate(LocalDate.now().plusDays(1))
        .productIds(Set.of(1L, 2L))
        .build();

    ResponseEntity<AvailabilityResponse> response = restTemplate.postForEntity(
        baseUrl() + availabilityUrl,
        request,
        AvailabilityResponse.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void givenEndDateBeforeStartDate_whenCheckAvailability_thenBadRequest() {
    AvailabilityRequest request = AvailabilityRequest.builder()
        .startDate(LocalDate.now().plusDays(5))
        .endDate(LocalDate.now().plusDays(1))
        .productIds(Set.of(1L, 2L))
        .build();

    ResponseEntity<AvailabilityResponse> response = restTemplate.postForEntity(
        baseUrl() + availabilityUrl,
        request,
        AvailabilityResponse.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void givenValidRequest_whenCheckAvailability_thenReturnAvailability() {
    AvailabilityRequest request = AvailabilityRequest.builder()
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(5))
        .productIds(Set.of(1L, 2L, 99L))
        .build();

    ResponseEntity<AvailabilityResponse> response = restTemplate.postForEntity(
        baseUrl() + availabilityUrl,
        request,
        AvailabilityResponse.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    AvailabilityResponse responseBody = response.getBody();
    assertThat(responseBody)
        .isNotNull()
        .extracting("startDate", "endDate")
        .containsExactly(request.getStartDate(), request.getEndDate());

    assertThat(responseBody.getAvailableUnitsByProduct())
        .hasSize(3)
        .containsEntry(1L, 0) // 2 operational items are booked in the test data
        .containsEntry(2L, 1) // 2 operational items but only 1 booked in the test data
        .containsEntry(99L, 0); // Non-existent product should have 0 availability
  }

  @Test
  void givenInvalidRequest_whenCreateBooking_thenBadRequest() {
    BookingRequest request = BookingRequest.builder()
        .startDate(LocalDate.now().minusDays(1))
        .endDate(LocalDate.now().minusDays(4)) // endDate before startDate
        .lines(null) // null lines
        .build();

    ResponseEntity<String> response = restTemplate.postForEntity(
        baseUrl() + bookingsUrl,
        request,
        String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void givenUnauthorizedUser_whenCreateBooking_thenUnauthorized() {
    BookingRequest request = BookingRequest.builder()
        .startDate(LocalDate.now().plusDays(1))
        .endDate(LocalDate.now().plusDays(2))
        .lines(List.of(
            LineRequest.builder().productId(2L).quantity(1).build()
        ))
        .build();

    ResponseEntity<String> response = restTemplate.postForEntity(
        baseUrl() + bookingsUrl,
        request,
        String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenInsufficientStock_whenCreateBooking_thenConflict() {
    BookingRequest request = BookingRequest.builder()
        .startDate(LocalDate.now().plusDays(1))
        .endDate(LocalDate.now().plusDays(2))
        .lines(List.of(
            LineRequest.builder().productId(1L).quantity(5).build() // Only 2 available in test data
        ))
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));

    HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(
        baseUrl() + bookingsUrl,
        entity,
        String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  void givenValidRequest_whenCreateBooking_thenBookingCreated() {
    BookingRequest request = BookingRequest.builder()
        .startDate(LocalDate.now().plusMonths(5))
        .endDate(LocalDate.now().plusMonths(6))
        .lines(List.of(
            LineRequest.builder().productId(2L).quantity(1).build()
        ))
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));

    HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Booking> response = restTemplate.postForEntity(
        baseUrl() + bookingsUrl,
        entity,
        Booking.class);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Booking booking = response.getBody();
    assertThat(booking).isNotNull();
    assertThat(booking.getId()).isNotNull();
    assertThat(booking.getLines()).hasSize(1);
    assertThat(booking).extracting("userId", "startDate", "endDate", "status")
        .containsExactly(
            Long.parseLong(currentUser.getId()),
            request.getStartDate(),
            request.getEndDate(),
            edu.uoc.epcsd.productcatalog.domain.booking.BookingStatus.PENDING);

    BookingLine bookingLine = booking.getLines().get(0);
    assertThat(bookingLine).isNotNull();
    assertThat(bookingLine.getId()).isNotNull();
    assertThat(bookingLine.getProduct()).isNotNull().extracting(Product::getId).isEqualTo(2L);
    assertThat(bookingLine.getQuantity()).isEqualTo(1);
  }

  @Test
  void givenUnauthorizedUser_whenGetBookings_thenUnauthorized() {
    HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());

    ResponseEntity<String> response = restTemplate.exchange(
        baseUrl() + bookingsUrl,
        HttpMethod.GET,
        entity,
        String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    response = restTemplate.exchange(
        baseUrl() + bookingsIdUrl,
        HttpMethod.GET,
        entity,
        String.class,
        Map.of("id", "1"));

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenNoBookings_whenGetBookings_thenEmptyList() {
    currentUser.setId("999");
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        baseUrl() + bookingsUrl,
        HttpMethod.GET,
        entity,
        String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertThat(response.getBody()).isNull();
  }

  @Test
  void givenBookings_whenGetBookings_thenListed() {
    currentUser.setId("5");
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Booking[]> response = restTemplate.exchange(
        baseUrl() + bookingsUrl,
        HttpMethod.GET,
        entity,
        Booking[].class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody()).isNotEmpty();
  }

  @Test
  void givenBooking_whenGetBooking_thenReturned() {
    currentUser.setId("5");
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Booking> response = restTemplate.exchange(
        baseUrl() + bookingsIdUrl,
        HttpMethod.GET,
        entity,
        Booking.class,
        Map.of("id", "1"));

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(1L);
  }

  @Test
  void givenBooking_whenGetBookingAndDoesNotBelongToUser_thenNotFound() {
    currentUser.setId("999");
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Booking> response = restTemplate.exchange(
        baseUrl() + bookingsIdUrl,
        HttpMethod.GET,
        entity,
        Booking.class,
        Map.of("id", "1"));

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}