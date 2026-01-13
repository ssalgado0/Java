package edu.uoc.epcsd.productcatalog.domain.repository;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);

  Optional<Booking> findById(long id);

  List<Booking> findByUserId(long userId);
}
