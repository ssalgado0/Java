package edu.uoc.epcsd.productcatalog.application.rest.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import edu.uoc.epcsd.productcatalog.domain.Product;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public final class GetProductResponse {

    private final Long id;

    private final String name;

    private final String description;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#0.00")
  private final BigDecimal dailyPrice;

    private final String brand;

    private final String model;

    private final Long categoryId;

    public static GetProductResponse fromDomain(Product product) {
        return GetProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .dailyPrice(product.getDailyPrice())
                .brand(product.getBrand())
                .model(product.getModel())
                .categoryId(product.getCategoryId())
                .build();
    }

}
