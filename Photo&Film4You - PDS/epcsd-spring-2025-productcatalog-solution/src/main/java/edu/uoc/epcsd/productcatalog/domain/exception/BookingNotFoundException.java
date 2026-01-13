package edu.uoc.epcsd.productcatalog.domain.exception;

public class BookingNotFoundException extends NotFoundException {

  public BookingNotFoundException(Long bookingId) {
    super("Booking with id " + bookingId + " not found.");
  }
}
