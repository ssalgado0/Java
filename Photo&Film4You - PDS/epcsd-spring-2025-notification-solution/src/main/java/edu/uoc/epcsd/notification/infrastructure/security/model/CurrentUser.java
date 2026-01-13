package edu.uoc.epcsd.notification.infrastructure.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUser {

  private String id;
  private String email;
  private String fullName;
  private String role;
}
