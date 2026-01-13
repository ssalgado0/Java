package edu.uoc.epcsd.auth.infrastructure.repository.rest;

import edu.uoc.epcsd.auth.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class GetUserResponse {

  private Long id;

  private String fullName;

  private String email;

  private String phoneNumber;

  private UserRole role;

}
