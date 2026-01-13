package edu.uoc.epcsd.productcatalog.application.rest.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateCategoryRequest {

    private final Long parentCategoryId;

    @NotBlank
    private final String name;

    private final String description;

}