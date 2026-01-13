package edu.uoc.epcsd.productcatalog.domain.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uoc.epcsd.productcatalog.domain.Product;
import java.math.BigDecimal;
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
public class BookingLine {

  private Long id;
  private Product product;
  private Integer quantity;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#0.00")
  private BigDecimal pricePerUnit;

  @JsonProperty("totalPrice")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#0.00")
  public BigDecimal getTotalPrice() {
    return this.pricePerUnit.multiply(BigDecimal.valueOf(this.quantity));
  }
}
