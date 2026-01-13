package edu.uoc.epcsd.digital.domain.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.digital.domain.repository.DigitalItemRepository;
import edu.uoc.epcsd.digital.domain.repository.DigitalSessionRepository;
import edu.uoc.epcsd.digital.infrastructure.security.model.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

  @Mock
  private DigitalSessionRepository digitalSessionRepository;

  @Mock
  private DigitalItemRepository digitalItemRepository;

  @InjectMocks
  private SecurityServiceImpl securityService;

  @Test
  void hasAccessToDigitalSession_ShouldReturnTrue_WhenRepositoryReturnsTrue() {
    Long sessionId = 1L;
    String email = "test@example.com";
    CurrentUser currentUser = new CurrentUser("1", email, "Test User", "USER");

    when(digitalSessionRepository.existsByIdAndEmail(sessionId, email)).thenReturn(true);

    boolean result = securityService.hasAccessToDigitalSession(sessionId, currentUser);

    assertTrue(result);
  }

  @Test
  void hasAccessToDigitalSession_ShouldReturnFalse_WhenRepositoryReturnsFalse() {
    Long sessionId = 1L;
    String email = "test@example.com";
    CurrentUser currentUser = new CurrentUser("1", email, "Test User", "USER");

    when(digitalSessionRepository.existsByIdAndEmail(sessionId, email)).thenReturn(false);

    boolean result = securityService.hasAccessToDigitalSession(sessionId, currentUser);

    assertFalse(result);
  }

  @Test
  void hasAccessToDigitalItem_ShouldReturnTrue_WhenRepositoryReturnsTrue() {
    Long itemId = 1L;
    String email = "test@example.com";
    CurrentUser currentUser = new CurrentUser("1", email, "Test User", "USER");

    when(digitalItemRepository.existsByIdAndEmail(itemId, email)).thenReturn(true);

    boolean result = securityService.hasAccessToDigitalItem(itemId, currentUser);

    assertTrue(result);
  }

  @Test
  void hasAccessToDigitalItem_ShouldReturnFalse_WhenRepositoryReturnsFalse() {
    Long itemId = 1L;
    String email = "test@example.com";
    CurrentUser currentUser = new CurrentUser("1", email, "Test User", "USER");

    when(digitalItemRepository.existsByIdAndEmail(itemId, email)).thenReturn(false);

    boolean result = securityService.hasAccessToDigitalItem(itemId, currentUser);

    assertFalse(result);
  }
}
