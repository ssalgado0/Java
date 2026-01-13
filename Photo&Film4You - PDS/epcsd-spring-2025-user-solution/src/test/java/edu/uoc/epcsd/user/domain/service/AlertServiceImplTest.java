package edu.uoc.epcsd.user.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.uoc.epcsd.user.domain.Alert;
import edu.uoc.epcsd.user.domain.exception.AlertAlreadyExistsException;
import edu.uoc.epcsd.user.domain.exception.ProductNotFoundException;
import edu.uoc.epcsd.user.domain.repository.AlertRepository;
import edu.uoc.epcsd.user.domain.repository.ProductRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertServiceImplTest {

  @Mock
  private AlertRepository alertRepository;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private AlertServiceImpl alertService;

  @Test
  void givenAlertsExist_whenFindAllAlerts_thenReturnAllAlerts() {
    // given
    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(100L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();
    
    Alert alert2 = Alert.builder()
        .id(2L)
        .productId(200L)
        .userId(2L)
        .from(LocalDate.of(2025, 2, 1))
        .to(LocalDate.of(2025, 2, 28))
        .build();
    
    List<Alert> expectedAlerts = Arrays.asList(alert1, alert2);
    when(alertRepository.findAllAlerts()).thenReturn(expectedAlerts);

    // when
    List<Alert> result = alertService.findAllAlerts();

    // then
    assertEquals(2, result.size());
    assertEquals(expectedAlerts, result);
    verify(alertRepository).findAllAlerts();
  }

  @Test
  void givenNoAlerts_whenFindAllAlerts_thenReturnEmptyList() {
    // given
    when(alertRepository.findAllAlerts()).thenReturn(Collections.emptyList());

    // when
    List<Alert> result = alertService.findAllAlerts();

    // then
    assertTrue(result.isEmpty());
    verify(alertRepository).findAllAlerts();
  }

  @Test
  void givenExistingAlertId_whenFindAlertById_thenReturnAlert() {
    // given
    Long alertId = 1L;
    Alert alert = Alert.builder()
        .id(alertId)
        .productId(100L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();
    
    when(alertRepository.findAlertById(alertId)).thenReturn(Optional.of(alert));

    // when
    Optional<Alert> result = alertService.findAlertById(alertId);

    // then
    assertTrue(result.isPresent());
    assertEquals(alert, result.get());
    verify(alertRepository).findAlertById(alertId);
  }

  @Test
  void givenNonExistingAlertId_whenFindAlertById_thenReturnEmpty() {
    // given
    Long alertId = 999L;
    when(alertRepository.findAlertById(alertId)).thenReturn(Optional.empty());

    // when
    Optional<Alert> result = alertService.findAlertById(alertId);

    // then
    assertFalse(result.isPresent());
    verify(alertRepository).findAlertById(alertId);
  }

  @Test
  void givenProductIdAndDate_whenFindAlertsByProductAndDate_thenReturnMatchingAlerts() {
    // given
    Long productId = 100L;
    LocalDate date = LocalDate.of(2025, 1, 15);
    
    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(productId)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();
    
    List<Alert> expectedAlerts = Collections.singletonList(alert1);
    when(alertRepository.findAlertsByProductAndDate(productId, date)).thenReturn(expectedAlerts);

    // when
    List<Alert> result = alertService.findAlertsByProductAndDate(productId, date);

    // then
    assertEquals(1, result.size());
    assertEquals(expectedAlerts, result);
    verify(alertRepository).findAlertsByProductAndDate(productId, date);
  }

  @Test
  void givenUserIdAndInterval_whenFindAlertsByUserAndInterval_thenReturnMatchingAlerts() {
    // given
    Long userId = 1L;
    LocalDate fromDate = LocalDate.of(2025, 1, 1);
    LocalDate toDate = LocalDate.of(2025, 3, 31);
    
    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(100L)
        .userId(userId)
        .from(LocalDate.of(2025, 1, 15))
        .to(LocalDate.of(2025, 2, 15))
        .build();
    
    Alert alert2 = Alert.builder()
        .id(2L)
        .productId(200L)
        .userId(userId)
        .from(LocalDate.of(2025, 2, 1))
        .to(LocalDate.of(2025, 3, 1))
        .build();
    
    List<Alert> expectedAlerts = Arrays.asList(alert1, alert2);
    when(alertRepository.findAlertsByUserAndInterval(userId, fromDate, toDate))
        .thenReturn(expectedAlerts);

    // when
    List<Alert> result = alertService.findAlertsByUserAndInterval(userId, fromDate, toDate);

    // then
    assertEquals(2, result.size());
    assertEquals(expectedAlerts, result);
    verify(alertRepository).findAlertsByUserAndInterval(userId, fromDate, toDate);
  }

  @Test
  void givenUserId_whenFindAlertsByUser_thenReturnUserAlerts() {
    // given
    Long userId = 1L;
    
    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(100L)
        .userId(userId)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();
    
    Alert alert2 = Alert.builder()
        .id(2L)
        .productId(200L)
        .userId(userId)
        .from(LocalDate.of(2025, 2, 1))
        .to(LocalDate.of(2025, 2, 28))
        .build();
    
    List<Alert> expectedAlerts = Arrays.asList(alert1, alert2);
    when(alertRepository.findAlertsByUser(userId)).thenReturn(expectedAlerts);

    // when
    List<Alert> result = alertService.findAlertsByUser(userId);

    // then
    assertEquals(2, result.size());
    assertEquals(expectedAlerts, result);
    verify(alertRepository).findAlertsByUser(userId);
  }

  @Test
  void givenValidAlertAndProductExists_whenCreateAlert_thenReturnAlertId() {
    // given
    Long expectedAlertId = 1L;
    Alert alert = Alert.builder()
        .productId(100L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();
    
    when(productRepository.existsById(alert.getProductId())).thenReturn(true);
    when(alertRepository.existsByProductIdAndUserIdAndFromAndTo(
        alert.getProductId(), alert.getUserId(), alert.getFrom(), alert.getTo()))
        .thenReturn(false);
    when(alertRepository.createAlert(alert)).thenReturn(expectedAlertId);

    // when
    Long result = alertService.createAlert(alert);

    // then
    assertEquals(expectedAlertId, result);
    verify(productRepository).existsById(alert.getProductId());
    verify(alertRepository).existsByProductIdAndUserIdAndFromAndTo(
        alert.getProductId(), alert.getUserId(), alert.getFrom(), alert.getTo());
    verify(alertRepository).createAlert(alert);
  }

  @Test
  void givenAlertWithNonExistingProduct_whenCreateAlert_thenThrowProductNotFoundException() {
    // given
    Alert alert = Alert.builder()
        .productId(999L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();
    
    when(productRepository.existsById(alert.getProductId())).thenReturn(false);

    // when & then
    assertThrows(ProductNotFoundException.class, () -> alertService.createAlert(alert));
    verify(productRepository).existsById(alert.getProductId());
    verify(alertRepository, never()).existsByProductIdAndUserIdAndFromAndTo(
        any(), any(), any(), any());
    verify(alertRepository, never()).createAlert(any());
  }

  @Test
  void givenDuplicateAlert_whenCreateAlert_thenThrowAlertAlreadyExistsException() {
    // given
    Alert alert = Alert.builder()
        .productId(100L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();
    
    when(productRepository.existsById(alert.getProductId())).thenReturn(true);
    when(alertRepository.existsByProductIdAndUserIdAndFromAndTo(
        alert.getProductId(), alert.getUserId(), alert.getFrom(), alert.getTo()))
        .thenReturn(true);

    // when & then
    assertThrows(AlertAlreadyExistsException.class, () -> alertService.createAlert(alert));
    verify(productRepository).existsById(alert.getProductId());
    verify(alertRepository).existsByProductIdAndUserIdAndFromAndTo(
        alert.getProductId(), alert.getUserId(), alert.getFrom(), alert.getTo());
    verify(alertRepository, never()).createAlert(any());
  }

  @Test
  void givenAlertId_whenRemoveAlert_thenAlertIsRemoved() {
    // given
    Long alertId = 1L;
    doNothing().when(alertRepository).removeAlert(alertId);

    // when
    alertService.removeAlert(alertId);

    // then
    verify(alertRepository).removeAlert(alertId);
  }

  @Test
  void givenMultipleAlerts_whenFindAlertsByProductAndDate_thenReturnOnlyMatchingAlerts() {
    // given
    Long productId = 100L;
    LocalDate date = LocalDate.of(2025, 6, 15);
    
    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(productId)
        .userId(1L)
        .from(LocalDate.of(2025, 6, 1))
        .to(LocalDate.of(2025, 6, 30))
        .build();
    
    Alert alert2 = Alert.builder()
        .id(2L)
        .productId(productId)
        .userId(2L)
        .from(LocalDate.of(2025, 6, 10))
        .to(LocalDate.of(2025, 6, 20))
        .build();
    
    List<Alert> expectedAlerts = Arrays.asList(alert1, alert2);
    when(alertRepository.findAlertsByProductAndDate(productId, date))
        .thenReturn(expectedAlerts);

    // when
    List<Alert> result = alertService.findAlertsByProductAndDate(productId, date);

    // then
    assertEquals(2, result.size());
    assertTrue(result.contains(alert1));
    assertTrue(result.contains(alert2));
    verify(alertRepository).findAlertsByProductAndDate(productId, date);
  }
}
