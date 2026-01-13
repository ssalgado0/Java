package edu.uoc.epcsd.user.domain.service;

import edu.uoc.epcsd.user.domain.repository.AlertRepository;
import edu.uoc.epcsd.user.infrastructure.security.model.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

  private final AlertRepository alertRepository;

  @Override
  @Transactional(readOnly = true)
  public boolean isSameUser(Long userId, CurrentUser currentUser) {
    return userId.toString().equals(currentUser.getId());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasAccessToAlert(Long alertId, CurrentUser currentUser) {
    return alertRepository.existsByIdAndUserId(alertId, Long.valueOf(currentUser.getId()));
  }
}
