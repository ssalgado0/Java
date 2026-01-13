package edu.uoc.epcsd.productcatalog.domain.booking;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

  private Long id;
  private Long userId;
  private LocalDate startDate;
  private LocalDate endDate;
  private BookingStatus status;
  private List<BookingLine> lines;
  private List<ItemAllocation> allocations;
}
