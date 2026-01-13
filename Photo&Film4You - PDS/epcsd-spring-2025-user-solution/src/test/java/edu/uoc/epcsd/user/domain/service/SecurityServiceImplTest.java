package edu.uoc.epcsd.user.domain.service;

import edu.uoc.epcsd.user.domain.repository.AlertRepository;
import edu.uoc.epcsd.user.infrastructure.security.model.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

  @Mock
  private AlertRepository alertRepository;

  @InjectMocks
  private SecurityServiceImpl securityService;

  private CurrentUser currentUser;

  @BeforeEach
  void setUp() {
    currentUser = new CurrentUser("123", "user@test.com", "Test User", "USER");
  }

  @Test
  void givenSameUserId_whenIsSameUser_thenReturnTrue() {
    // given
    Long userId = 123L;

    // when
    boolean result = securityService.isSameUser(userId, currentUser);

    // then
    assertTrue(result);
  }

  @Test
  void givenDifferentUserId_whenIsSameUser_thenReturnFalse() {
    // given
    Long userId = 999L;

    // when
    boolean result = securityService.isSameUser(userId, currentUser);

    // then
    assertFalse(result);
  }

  @Test
  void givenUserOwnsAlert_whenHasAccessToAlert_thenReturnTrue() {
    // given
    Long alertId = 1L;
    when(alertRepository.existsByIdAndUserId(alertId, 123L)).thenReturn(true);

    // when
    boolean result = securityService.hasAccessToAlert(alertId, currentUser);

    // then
    assertTrue(result);
  }

  @Test
  void givenUserDoesNotOwnAlert_whenHasAccessToAlert_thenReturnFalse() {
    // given
    Long alertId = 1L;
    when(alertRepository.existsByIdAndUserId(alertId, 123L)).thenReturn(false);

    // when
    boolean result = securityService.hasAccessToAlert(alertId, currentUser);

    // then
    assertFalse(result);
  }
}
