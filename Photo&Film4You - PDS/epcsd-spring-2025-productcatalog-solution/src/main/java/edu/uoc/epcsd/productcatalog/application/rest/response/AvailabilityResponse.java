package edu.uoc.epcsd.productcatalog.application.rest.response;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class AvailabilityResponse {

  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate startDate;
  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate endDate;
  private Map<Long, Integer> availableUnitsByProduct;

}
