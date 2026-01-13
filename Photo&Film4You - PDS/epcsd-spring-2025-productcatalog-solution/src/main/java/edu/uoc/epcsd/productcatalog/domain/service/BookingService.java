package edu.uoc.epcsd.productcatalog.domain.service;

import edu.uoc.epcsd.productcatalog.application.rest.request.AvailabilityRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.BookingRequest;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import java.util.List;
import java.util.Map;

public interface BookingService {

  /**
   * Check availability of items for the given request.
   *
   * @param availabilityRequest the availability request containing product IDs and date range
   * @return a map of product IDs to available quantities. E.g.: {@code {1: 5, 2: 0, 3: 10}}
   */
  Map<Long, Integer> checkAvailability(AvailabilityRequest availabilityRequest);

  Booking createBooking(BookingRequest bookingRequest, CurrentUser currentUser);

  Booking getBookingById(Long bookingId, CurrentUser currentUser);

  List<Booking> getAllBookingsFromUser(CurrentUser currentUser);
}
