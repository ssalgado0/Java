package edu.uoc.epcsd.digital.application.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateDigitalItemRequest {

  @NotNull(message = "digitalSessionId is required")
  private final Long digitalSessionId;

  @NotBlank(message = "description is required")
  private final String description;

  @NotNull(message = "lat is required")
  private final Double lat;

  @NotNull(message = "lon is required")
  private final Double lon;

  @NotBlank(message = "link is required")
  private final String link;
}
