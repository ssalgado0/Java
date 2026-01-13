package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataBookingRepository extends JpaRepository<BookingEntity, Long> {

  List<BookingEntity> findByUserId(Long userId);
}
