package edu.uoc.epcsd.digital.domain.service;

import edu.uoc.epcsd.digital.infrastructure.security.model.CurrentUser;

public interface SecurityService {

  boolean hasAccessToDigitalSession(Long digitalSessionId, CurrentUser currentUser);

  boolean hasAccessToDigitalItem(Long digitalItemId, CurrentUser currentUser);
}
