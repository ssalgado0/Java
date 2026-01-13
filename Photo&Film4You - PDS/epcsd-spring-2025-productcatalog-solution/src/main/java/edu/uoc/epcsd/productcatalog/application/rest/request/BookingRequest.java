package edu.uoc.epcsd.productcatalog.application.rest.request;

import edu.uoc.epcsd.productcatalog.application.rest.request.interfaces.HasDateRange;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class BookingRequest implements HasDateRange {

  @NotNull(message = "startDate is mandatory")
  @FutureOrPresent(message = "startDate must be today or in the future")
  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate startDate;

  @NotNull(message = "endDate is mandatory")
  @Future(message = "endDate must be in the future")
  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate endDate;

  @NotEmpty(message = "lines cannot be empty")
  private List<@Valid LineRequest> lines;
}