package edu.uoc.epcsd.digital.application.rest.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateDigitalSessionRequest {

  @NotBlank(message = "email is required")
  @Email(message = "Invalid email format")
  private final String email;

  @NotBlank(message = "description is required")
  private final String description;
}
