package edu.uoc.epcsd.productcatalog.domain.booking;

import java.time.LocalDate;
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
public class ItemAllocation {

  private Long id;
  private String itemSerialNumber;
  private LocalDate startDate;
  private LocalDate endDate;

}
