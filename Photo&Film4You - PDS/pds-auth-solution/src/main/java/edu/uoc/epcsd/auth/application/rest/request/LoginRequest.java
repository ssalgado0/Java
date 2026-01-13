package edu.uoc.epcsd.auth.application.rest.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class LoginRequest {

  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email should be valid")
  private String email;

  @NotBlank(message = "Password must not be blank")
  private String password;
}
