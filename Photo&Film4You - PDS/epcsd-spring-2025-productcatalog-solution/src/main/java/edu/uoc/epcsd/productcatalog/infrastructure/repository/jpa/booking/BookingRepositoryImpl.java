package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.domain.booking.BookingLine;
import edu.uoc.epcsd.productcatalog.domain.exception.ProductNotFoundException;
import edu.uoc.epcsd.productcatalog.domain.mapper.BookingMapper;
import edu.uoc.epcsd.productcatalog.domain.repository.BookingRepository;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ProductEntity;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.SpringDataProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

  private final SpringDataBookingRepository jpaRepository;
  private final SpringDataBookingLineRepository jpaBookingLineRepository;
  private final SpringDataProductRepository jpaProductRepository;
  private final BookingMapper bookingMapper;

  @Override
  public Booking save(Booking booking) {
    BookingEntity entity = bookingMapper.toEntity(booking);
    jpaRepository.saveAndFlush(entity);

    entity.getLines().addAll(getBookingLines(entity, booking.getLines()));

    BookingEntity savedEntity = jpaRepository.saveAndFlush(entity);
    return bookingMapper.toDomain(savedEntity);
  }

  @Override
  public Optional<Booking> findById(long id) {
    return jpaRepository.findById(id)
        .map(bookingMapper::toDomain);
  }

  @Override
  public List<Booking> findByUserId(long userId) {
    return bookingMapper.toDomain(jpaRepository.findByUserId(userId));
  }

  List<BookingLineEntity> getBookingLines(BookingEntity booking, List<BookingLine> lines) {
    return jpaBookingLineRepository.saveAllAndFlush(CollectionUtils.emptyIfNull(lines).stream()
        .map(l -> {
          ProductEntity p = jpaProductRepository.findById(l.getProduct().getId())
              .orElseThrow(() -> new ProductNotFoundException(l.getProduct().getId()));
          return bookingMapper.toEntity(l, booking, p);
        })
        .collect(Collectors.toSet()));
  }
}