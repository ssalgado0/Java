package edu.uoc.epcsd.digital.infrastructure.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CurrentUser {

  private String id;
  private String email;
  private String fullName;
  private String role;
}