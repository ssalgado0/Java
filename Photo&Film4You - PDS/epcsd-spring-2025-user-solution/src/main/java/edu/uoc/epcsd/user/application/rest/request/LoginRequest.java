package edu.uoc.epcsd.user.application.rest.request;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public final class LoginRequest {

  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email should be valid")
  private final String email;

  @NotBlank(message = "Password must not be blank")
  private final String password;
}
