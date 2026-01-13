package edu.uoc.epcsd.productcatalog.application.rest.request;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull @Min(0)
    private BigDecimal dailyPrice;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    private Long categoryId;

}
