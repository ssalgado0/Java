package edu.uoc.epcsd.notification.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.uoc.epcsd.notification.application.kafka.DigitalItemMessage;
import edu.uoc.epcsd.notification.application.rest.dtos.GetDigitalItemResponse;
import edu.uoc.epcsd.notification.domain.Notification;
import edu.uoc.epcsd.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.NotFoundException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  void setUp() {

    ReflectionTestUtils.setField(notificationService, "digitalItemServiceUrl", "http://localhost:8080/api/items/{id}");
    ReflectionTestUtils.setField(notificationService, "userServiceUrl", "http://localhost:8080/api/users");
    ReflectionTestUtils.setField(notificationService, "productServiceUrl", "http://localhost:8080/api/products");
  }
  @Test
  void givenUserId_whenFindAllByUserId_thenReturnUserNotifications() {
    // given
    Long userId = 1L;
    Notification notification1 = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("Test Notification 1")
        .message("Message 1")
        .type("info")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    Notification notification2 = Notification.builder()
        .id(2L)
        .userId(userId)
        .title("Test Notification 2")
        .message("Message 2")
        .type("warning")
        .createdAt(LocalDateTime.now())
        .read(true)
        .build();

    List<Notification> expectedNotifications = Arrays.asList(notification1, notification2);
    when(notificationRepository.findAllByUserId(userId)).thenReturn(expectedNotifications);

    // when
    List<Notification> result = notificationService.findAllByUserId(userId);

    // then
    assertEquals(2, result.size());
    assertEquals(expectedNotifications, result);
    verify(notificationRepository).findAllByUserId(userId);
  }

  @Test
  void givenUserIdWithNoNotifications_whenFindAllByUserId_thenReturnEmptyList() {
    // given
    Long userId = 999L;
    when(notificationRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

    // when
    List<Notification> result = notificationService.findAllByUserId(userId);

    // then
    assertTrue(result.isEmpty());
    verify(notificationRepository).findAllByUserId(userId);
  }

  @Test
  void givenUserId_whenFindUnreadByUserId_thenReturnOnlyUnreadNotifications() {
    // given
    Long userId = 1L;
    Notification unreadNotification1 = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("Unread Notification 1")
        .message("Message 1")
        .type("info")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    Notification unreadNotification2 = Notification.builder()
        .id(3L)
        .userId(userId)
        .title("Unread Notification 2")
        .message("Message 3")
        .type("error")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    List<Notification> expectedNotifications = Arrays.asList(unreadNotification1,
        unreadNotification2);
    when(notificationRepository.findUnreadByUserId(userId)).thenReturn(expectedNotifications);

    // when
    List<Notification> result = notificationService.findUnreadByUserId(userId);

    // then
    assertEquals(2, result.size());
    assertTrue(result.stream().noneMatch(Notification::getRead));
    verify(notificationRepository).findUnreadByUserId(userId);
  }

  @Test
  void givenUserIdWithNoUnreadNotifications_whenFindUnreadByUserId_thenReturnEmptyList() {
    // given
    Long userId = 1L;
    when(notificationRepository.findUnreadByUserId(userId)).thenReturn(Collections.emptyList());

    // when
    List<Notification> result = notificationService.findUnreadByUserId(userId);

    // then
    assertTrue(result.isEmpty());
    verify(notificationRepository).findUnreadByUserId(userId);
  }

  @Test
  void givenExistingNotificationId_whenFindById_thenReturnNotification() {
    // given
    Long notificationId = 1L;
    Notification notification = Notification.builder()
        .id(notificationId)
        .userId(1L)
        .title("Test Notification")
        .message("Test Message")
        .type("info")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

    // when
    Optional<Notification> result = notificationService.findById(notificationId);

    // then
    assertTrue(result.isPresent());
    assertEquals(notification, result.get());
    verify(notificationRepository).findById(notificationId);
  }

  @Test
  void givenNonExistingNotificationId_whenFindById_thenReturnEmpty() {
    // given
    Long notificationId = 999L;
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

    // when
    Optional<Notification> result = notificationService.findById(notificationId);

    // then
    assertFalse(result.isPresent());
    verify(notificationRepository).findById(notificationId);
  }

  @Test
  void givenValidNotification_whenCreateNotification_thenReturnNotificationId() {
    // given
    Long expectedId = 1L;
    Notification notification = Notification.builder()
        .userId(1L)
        .title("New Notification")
        .message("New Message")
        .type("success")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    when(notificationRepository.createNotification(notification)).thenReturn(expectedId);

    // when
    Long result = notificationService.createNotification(notification);

    // then
    assertEquals(expectedId, result);
    verify(notificationRepository).createNotification(notification);
  }

  @Test
  void givenNotificationId_whenMarkAsRead_thenNotificationIsMarkedAsRead() {
    // given
    Long notificationId = 1L;
    doNothing().when(notificationRepository).markAsRead(notificationId);

    // when
    notificationService.markAsRead(notificationId);

    // then
    verify(notificationRepository).markAsRead(notificationId);
  }

  @Test
  void givenUserId_whenMarkAllAsReadByUserId_thenAllUserNotificationsAreMarkedAsRead() {
    // given
    Long userId = 1L;
    doNothing().when(notificationRepository).markAllAsReadByUserId(userId);

    // when
    notificationService.markAllAsReadByUserId(userId);

    // then
    verify(notificationRepository).markAllAsReadByUserId(userId);
  }

  @Test
  void givenMultipleUnreadNotifications_whenFindUnreadByUserId_thenReturnAllUnread() {
    // given
    Long userId = 2L;
    Notification notification1 = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("Notification 1")
        .message("Message 1")
        .type("info")
        .createdAt(LocalDateTime.now().minusHours(2))
        .read(false)
        .build();

    Notification notification2 = Notification.builder()
        .id(2L)
        .userId(userId)
        .title("Notification 2")
        .message("Message 2")
        .type("warning")
        .createdAt(LocalDateTime.now().minusHours(1))
        .read(false)
        .build();

    Notification notification3 = Notification.builder()
        .id(3L)
        .userId(userId)
        .title("Notification 3")
        .message("Message 3")
        .type("error")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    List<Notification> expectedNotifications = Arrays.asList(notification1, notification2,
        notification3);
    when(notificationRepository.findUnreadByUserId(userId)).thenReturn(expectedNotifications);

    // when
    List<Notification> result = notificationService.findUnreadByUserId(userId);

    // then
    assertEquals(3, result.size());
    assertTrue(result.stream().allMatch(n -> !n.getRead()));
    verify(notificationRepository).findUnreadByUserId(userId);
  }

  @Test
  void givenMixedReadAndUnreadNotifications_whenFindAllByUserId_thenReturnAll() {
    // given
    Long userId = 1L;
    Notification readNotification = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("Read Notification")
        .message("This is read")
        .type("info")
        .createdAt(LocalDateTime.now().minusDays(1))
        .read(true)
        .build();

    Notification unreadNotification = Notification.builder()
        .id(2L)
        .userId(userId)
        .title("Unread Notification")
        .message("This is unread")
        .type("warning")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    List<Notification> expectedNotifications = Arrays.asList(readNotification,
        unreadNotification);
    when(notificationRepository.findAllByUserId(userId)).thenReturn(expectedNotifications);

    // when
    List<Notification> result = notificationService.findAllByUserId(userId);

    // then
    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(Notification::getRead));
    assertTrue(result.stream().anyMatch(n -> !n.getRead()));
    verify(notificationRepository).findAllByUserId(userId);
  }

  @Test
  void givenNotificationWithDifferentTypes_whenCreateNotification_thenReturnId() {
    // given
    String[] types = {"info", "warning", "error", "success"};

    for (String type : types) {
      Long expectedId = 1L;
      Notification notification = Notification.builder()
          .userId(1L)
          .title("Notification with type " + type)
          .message("Message")
          .type(type)
          .createdAt(LocalDateTime.now())
          .read(false)
          .build();

      when(notificationRepository.createNotification(any(Notification.class)))
          .thenReturn(expectedId);

      // when
      Long result = notificationService.createNotification(notification);

      // then
      assertEquals(expectedId, result);
    }

    verify(notificationRepository, times(4)).createNotification(any(Notification.class));
  }

  @Test
  void givenKafkaMessageWithAVAILABLEDigitalItem_whenProcessing_shouldNotCreateNotification() {

    Long digitalItemId = 123L;
    DigitalItemMessage message = new DigitalItemMessage(digitalItemId);

    GetDigitalItemResponse mockResponse = GetDigitalItemResponse.builder()
            .id(digitalItemId)
            .status(GetDigitalItemResponse.DigitalStatus.AVAILABLE) // Diferente a REVIEW_PENDING
            .build();


    when(restTemplate.getForEntity(anyString(), eq(GetDigitalItemResponse.class), eq(digitalItemId)))
            .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    notificationService.notifyDigitalItemPendingReview(message);

    verify(notificationRepository, never()).createNotification(any(Notification.class));

  }

  @Test
  void givenKafkaMessageWithPENDINGREVIEWDigitalItem_whenProcessing_shouldCreateNotification() {

    Long digitalItemId = 123L;
    DigitalItemMessage message = new DigitalItemMessage(digitalItemId);

    GetDigitalItemResponse mockResponse = GetDigitalItemResponse.builder()
            .id(digitalItemId)
            .status(GetDigitalItemResponse.DigitalStatus.REVIEW_PENDING) // Diferente a REVIEW_PENDING
            .build();


    when(restTemplate.getForEntity(anyString(), eq(GetDigitalItemResponse.class), eq(digitalItemId)))
            .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    notificationService.notifyDigitalItemPendingReview(message);

    verify(notificationRepository, times(1)).createNotification(any(Notification.class));

  }

  @Test
  void givenKafkaMessageWithDigitalItem_whenProcessingAndReceivingNULL_shouldThrowException() {

    Long digitalItemId = 123L;
    DigitalItemMessage message = new DigitalItemMessage(digitalItemId);
    GetDigitalItemResponse mockResponse = null;
    when(restTemplate.getForEntity(anyString(), eq(GetDigitalItemResponse.class), eq(digitalItemId)))
            .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
    assertThrows(NotFoundException.class, () -> notificationService.notifyDigitalItemPendingReview(message));

  }
}
