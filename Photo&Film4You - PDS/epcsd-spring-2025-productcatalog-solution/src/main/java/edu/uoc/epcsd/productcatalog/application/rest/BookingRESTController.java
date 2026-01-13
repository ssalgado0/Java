package edu.uoc.epcsd.productcatalog.application.rest;

import edu.uoc.epcsd.productcatalog.application.rest.request.AvailabilityRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.BookingRequest;
import edu.uoc.epcsd.productcatalog.application.rest.response.AvailabilityResponse;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.domain.service.BookingService;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingRESTController {

  private final BookingService bookingService;

  @PostMapping("/availability")
  public ResponseEntity<AvailabilityResponse> checkAvailability(
      @RequestBody @Valid AvailabilityRequest request) {
    log.info("Received request to check availability");
    Map<Long, Integer> availableUnitsByProduct = bookingService.checkAvailability(request);
    return ResponseEntity.ok(AvailabilityResponse.builder()
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .availableUnitsByProduct(availableUnitsByProduct)
        .build());
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  @PostMapping
  public ResponseEntity<Booking> createBooking(@RequestBody @Valid BookingRequest bookingRequest,
      @AuthenticationPrincipal CurrentUser currentUser) {
    log.trace("Creating booking {}", bookingRequest);

    Booking createdBooking = bookingService.createBooking(bookingRequest, currentUser);

    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdBooking.getId())
            .toUri();

    return ResponseEntity.created(location).body(createdBooking);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  @GetMapping("/{id}")
  public ResponseEntity<Booking> getBooking(@PathVariable Long id,
      @AuthenticationPrincipal CurrentUser currentUser) {
    log.trace("Getting booking with id: {}", id);

    Booking booking = bookingService.getBookingById(id, currentUser);

    return ResponseEntity.ok(booking);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  @GetMapping
  public ResponseEntity<List<Booking>> getUserBookings(
      @AuthenticationPrincipal CurrentUser currentUser) {
    log.trace("Getting all bookings from user: {}", currentUser);

    List<Booking> userBookings = bookingService.getAllBookingsFromUser(currentUser);

    if (userBookings.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(userBookings);
  }
}
