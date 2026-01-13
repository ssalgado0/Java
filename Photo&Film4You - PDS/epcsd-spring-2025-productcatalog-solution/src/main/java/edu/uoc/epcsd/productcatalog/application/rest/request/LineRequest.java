package edu.uoc.epcsd.productcatalog.application.rest.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class LineRequest {

  @NotNull(message = "productId must not be null")
  private Long productId;

  @NotNull(message = "quantity must not be null")
  @Positive(message = "quantity must be positive")
  private Integer quantity;
}
