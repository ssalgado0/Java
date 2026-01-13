package edu.uoc.epcsd.productcatalog.infrastructure.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CurrentUser {

  private String id;
  private String email;
  private String fullName;
  private String role;
}