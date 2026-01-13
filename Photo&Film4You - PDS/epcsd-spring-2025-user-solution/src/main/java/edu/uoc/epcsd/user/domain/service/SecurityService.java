package edu.uoc.epcsd.user.domain.service;

import edu.uoc.epcsd.user.infrastructure.security.model.CurrentUser;

public interface SecurityService {
  boolean isSameUser(Long userId, CurrentUser currentUser);

  boolean hasAccessToAlert(Long alertId, CurrentUser currentUser);
}
