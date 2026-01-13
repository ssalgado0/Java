package edu.uoc.epcsd.auth.domain;

import edu.uoc.epcsd.auth.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  private Long id;
  private String fullName;
  private String email;
  private String phoneNumber;
  private UserRole role;
}
