package edu.uoc.epcsd.digital.domain.service;

import edu.uoc.epcsd.digital.domain.repository.DigitalItemRepository;
import edu.uoc.epcsd.digital.domain.repository.DigitalSessionRepository;
import edu.uoc.epcsd.digital.infrastructure.security.model.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

  private final DigitalSessionRepository digitalSessionRepository;
  private final DigitalItemRepository digitalItemRepository;

  @Override
  @Transactional(readOnly = true)
  public boolean hasAccessToDigitalSession(Long digitalSessionId, CurrentUser currentUser) {
    final String email = currentUser.getEmail();
    return digitalSessionRepository.existsByIdAndEmail(digitalSessionId, email);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasAccessToDigitalItem(Long digitalItemId, CurrentUser currentUser) {
    final String email = currentUser.getEmail();
    return digitalItemRepository.existsByIdAndEmail(digitalItemId, email);
  }
}
