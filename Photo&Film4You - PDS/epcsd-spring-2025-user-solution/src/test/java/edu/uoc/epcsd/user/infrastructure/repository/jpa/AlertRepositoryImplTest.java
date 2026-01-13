package edu.uoc.epcsd.user.infrastructure.repository.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.user.domain.Alert;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertRepositoryImplTest {

  @Mock
  private SpringDataAlertRepository jpaRepository;

  @Mock
  private SpringDataUserRepository jpaUserRepository;

  @InjectMocks
  private AlertRepositoryImpl alertRepository;

  private AlertEntity alertEntity;
  private UserEntity userEntity;
  private Alert alert;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setEmail("user@test.com");
    userEntity.setPassword("password");
    userEntity.setFullName("Test User");

    alertEntity = new AlertEntity();
    alertEntity.setId(1L);
    alertEntity.setProductId(100L);
    alertEntity.setUser(userEntity);
    alertEntity.setFrom(LocalDate.of(2025, 1, 1));
    alertEntity.setTo(LocalDate.of(2025, 12, 31));

    alert = Alert.builder()
        .id(1L)
        .productId(100L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 12, 31))
        .build();
  }

  @Test
  void givenAlerts_whenFindAllAlerts_thenReturnList() {
    // given
    when(jpaRepository.findAll()).thenReturn(Arrays.asList(alertEntity));

    // when
    List<Alert> result = alertRepository.findAllAlerts();

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jpaRepository).findAll();
  }

  @Test
  void givenAlertId_whenFindAlertById_thenReturnAlert() {
    // given
    when(jpaRepository.findById(1L)).thenReturn(Optional.of(alertEntity));

    // when
    Optional<Alert> result = alertRepository.findAlertById(1L);

    // then
    assertTrue(result.isPresent());
    assertEquals(100L, result.get().getProductId());
    verify(jpaRepository).findById(1L);
  }

  @Test
  void givenProductIdAndDate_whenFindAlertsByProductAndDate_thenReturnList() {
    // given
    LocalDate date = LocalDate.of(2025, 6, 15);
    when(jpaRepository.findAllByProductIdAndToGreaterThanEqual(100L, date))
        .thenReturn(Arrays.asList(alertEntity));

    // when
    List<Alert> result = alertRepository.findAlertsByProductAndDate(100L, date);

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jpaRepository).findAllByProductIdAndToGreaterThanEqual(100L, date);
  }

  @Test
  void givenUserIdAndInterval_whenFindAlertsByUserAndInterval_thenReturnList() {
    // given
    LocalDate fromDate = LocalDate.of(2025, 1, 1);
    LocalDate toDate = LocalDate.of(2025, 12, 31);
    when(jpaRepository.findAlertsByUserAndInterval(1L, fromDate, toDate))
        .thenReturn(Arrays.asList(alertEntity));

    // when
    List<Alert> result = alertRepository.findAlertsByUserAndInterval(1L, fromDate, toDate);

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jpaRepository).findAlertsByUserAndInterval(1L, fromDate, toDate);
  }

  @Test
  void givenUserId_whenFindAlertsByUser_thenReturnList() {
    // given
    when(jpaRepository.findAlertsByUser(1L)).thenReturn(Arrays.asList(alertEntity));

    // when
    List<Alert> result = alertRepository.findAlertsByUser(1L);

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jpaRepository).findAlertsByUser(1L);
  }

  @Test
  void givenAlert_whenCreateAlert_thenReturnId() {
    // given
    when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    AlertEntity savedEntity = new AlertEntity();
    savedEntity.setId(10L);
    when(jpaRepository.save(any(AlertEntity.class))).thenReturn(savedEntity);

    // when
    Long result = alertRepository.createAlert(alert);

    // then
    assertEquals(10L, result);
    verify(jpaUserRepository).findById(1L);
    verify(jpaRepository).save(any(AlertEntity.class));
  }

  @Test
  void givenInvalidUserId_whenCreateAlert_thenThrowException() {
    // given
    when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(IllegalArgumentException.class, () -> alertRepository.createAlert(alert));
    verify(jpaUserRepository).findById(1L);
    verify(jpaRepository, never()).save(any(AlertEntity.class));
  }

  @Test
  void givenAlertExists_whenExistsByProductIdAndUserIdAndFromAndTo_thenReturnTrue() {
    // given
    LocalDate from = LocalDate.of(2025, 1, 1);
    LocalDate to = LocalDate.of(2025, 12, 31);
    when(jpaRepository.existsByProductIdAndUserIdAndFromAndTo(100L, 1L, from, to)).thenReturn(true);

    // when
    boolean result = alertRepository.existsByProductIdAndUserIdAndFromAndTo(100L, 1L, from, to);

    // then
    assertTrue(result);
    verify(jpaRepository).existsByProductIdAndUserIdAndFromAndTo(100L, 1L, from, to);
  }

  @Test
  void givenAlertDoesNotExist_whenExistsByProductIdAndUserIdAndFromAndTo_thenReturnFalse() {
    // given
    LocalDate from = LocalDate.of(2025, 1, 1);
    LocalDate to = LocalDate.of(2025, 12, 31);
    when(jpaRepository.existsByProductIdAndUserIdAndFromAndTo(100L, 1L, from, to)).thenReturn(false);

    // when
    boolean result = alertRepository.existsByProductIdAndUserIdAndFromAndTo(100L, 1L, from, to);

    // then
    assertFalse(result);
    verify(jpaRepository).existsByProductIdAndUserIdAndFromAndTo(100L, 1L, from, to);
  }

  @Test
  void givenUserOwnsAlert_whenExistsByIdAndUserId_thenReturnTrue() {
    // given
    when(jpaRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);

    // when
    boolean result = alertRepository.existsByIdAndUserId(1L, 1L);

    // then
    assertTrue(result);
    verify(jpaRepository).existsByIdAndUserId(1L, 1L);
  }

  @Test
  void givenUserDoesNotOwnAlert_whenExistsByIdAndUserId_thenReturnFalse() {
    // given
    when(jpaRepository.existsByIdAndUserId(1L, 999L)).thenReturn(false);

    // when
    boolean result = alertRepository.existsByIdAndUserId(1L, 999L);

    // then
    assertFalse(result);
    verify(jpaRepository).existsByIdAndUserId(1L, 999L);
  }

  @Test
  void givenAlertId_whenRemoveAlert_thenDeleteById() {
    // given
    Long alertId = 1L;

    // when
    alertRepository.removeAlert(alertId);

    // then
    verify(jpaRepository).deleteById(alertId);
  }
}
