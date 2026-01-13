package edu.uoc.epcsd.productcatalog.domain.service;

import edu.uoc.epcsd.productcatalog.application.rest.request.AvailabilityRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.BookingRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.LineRequest;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.domain.exception.BookingNotFoundException;
import edu.uoc.epcsd.productcatalog.domain.exception.ProductStockException;
import edu.uoc.epcsd.productcatalog.domain.mapper.BookingMapper;
import edu.uoc.epcsd.productcatalog.domain.repository.BookingRepository;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingLineService bookingLineService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
  private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> checkAvailability(AvailabilityRequest request) {
        log.info("Checking availability for request: {}", request);

        Set<Long> productIds = request.getProductIds();

        Map<Long, Integer> totalStock = itemService.countItemsByProductIdIn(productIds);

        Map<Long, Integer> reservedStock = bookingLineService.countReservedItemsPerProductAndDateRange(
                productIds, request.getStartDate(), request.getEndDate());

        return productIds.stream()
                .collect(Collectors.toMap(
                        productId -> productId,
                        productId -> {
                            int total = totalStock.getOrDefault(productId, 0);
                            int occupied = reservedStock.getOrDefault(productId, 0);
                            return Math.max(total - occupied, 0);
                        }));
    }

    @Override
    @Transactional
    public Booking createBooking(BookingRequest bookingRequest, CurrentUser currentUser) {
      log.info("Creating booking for request: {}", bookingRequest);

      Set<Long> productIds = bookingRequest.getLines().stream()
          .map(LineRequest::getProductId)
                .collect(Collectors.toSet());

        AvailabilityRequest availabilityRequest = AvailabilityRequest.builder()
            .startDate(bookingRequest.getStartDate())
            .endDate(bookingRequest.getEndDate())
                .productIds(productIds)
                .build();

        Map<Long, Integer> availability = checkAvailability(availabilityRequest);

      for (LineRequest line : bookingRequest.getLines()) {
            Integer availableQty = availability.getOrDefault(line.getProductId(), 0);

            if (availableQty < line.getQuantity()) {
              log.warn("Insufficient stock for product ID {}: requested {}, available {}",
                  line.getProductId(), line.getQuantity(), availableQty);
              throw new ProductStockException(line.getProductId(), line.getQuantity());
            }
        }

      log.info("Products available, proceeding to create booking");

      Booking booking = bookingMapper.toDomain(bookingRequest, currentUser);
        return bookingRepository.save(booking);
    }

  @Override
  public Booking getBookingById(Long bookingId, CurrentUser currentUser) {
    final long userId = Long.parseLong(currentUser.getId());
    return bookingRepository.findById(bookingId)
        .filter(b -> Objects.equals(b.getUserId(), userId))
        .orElseThrow(() -> new BookingNotFoundException(bookingId));
  }

  @Override
  public List<Booking> getAllBookingsFromUser(CurrentUser currentUser) {
    return bookingRepository.findByUserId(Long.parseLong(currentUser.getId()));
  }
}