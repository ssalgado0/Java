package edu.uoc.epcsd.notification.infrastructure.repository.jpa;

import edu.uoc.epcsd.notification.domain.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationRepositoryImplTest {

  @Mock
  private SpringDataNotificationRepository jpaRepository;

  @InjectMocks
  private NotificationRepositoryImpl notificationRepository;

  private NotificationEntity notificationEntity;
  private Notification notification;

  @BeforeEach
  void setUp() {
    notificationEntity = NotificationEntity.builder()
        .id(1L)
        .userId(100L)
        .title("Test Title")
        .message("Test Message")
        .type("info")
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .read(false)
            .entityReference("GENERAL")
        .build();

    notification = Notification.builder()
        .id(1L)
        .userId(100L)
        .title("Test Title")
        .message("Test Message")
        .type("info")
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .read(false).entity(Notification.EntityReference.GENERAL)
        .build();
  }

  @Test
  void givenUserId_whenFindAllByUserId_thenReturnList() {
    // given
    when(jpaRepository.findAllByUserId(100L)).thenReturn(Arrays.asList(notificationEntity));

    // when
    List<Notification> result = notificationRepository.findAllByUserId(100L);

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Test Title", result.get(0).getTitle());
    verify(jpaRepository).findAllByUserId(100L);
  }

  @Test
  void givenUserId_whenFindUnreadByUserId_thenReturnUnreadList() {
    // given
    when(jpaRepository.findUnreadByUserId(100L)).thenReturn(Arrays.asList(notificationEntity));

    // when
    List<Notification> result = notificationRepository.findUnreadByUserId(100L);

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertFalse(result.get(0).getRead());
    verify(jpaRepository).findUnreadByUserId(100L);
  }

  @Test
  void givenNotificationId_whenFindById_thenReturnNotification() {
    // given
    when(jpaRepository.findById(1L)).thenReturn(Optional.of(notificationEntity));

    // when
    Optional<Notification> result = notificationRepository.findById(1L);

    // then
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals("Test Title", result.get().getTitle());
    verify(jpaRepository).findById(1L);
  }

  @Test
  void givenNonExistingId_whenFindById_thenReturnEmpty() {
    // given
    when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

    // when
    Optional<Notification> result = notificationRepository.findById(999L);

    // then
    assertFalse(result.isPresent());
    verify(jpaRepository).findById(999L);
  }

  @Test
  void givenNotification_whenCreateNotification_thenReturnId() {
    // given
    NotificationEntity savedEntity = NotificationEntity.builder()
        .id(10L)
        .userId(100L)
        .title("Test Title")
        .message("Test Message")
        .type("info")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    when(jpaRepository.save(any(NotificationEntity.class))).thenReturn(savedEntity);

    // when
    Long result = notificationRepository.createNotification(notification);

    // then
    assertEquals(10L, result);
    verify(jpaRepository).save(any(NotificationEntity.class));
  }

  @Test
  void givenNotificationId_whenMarkAsRead_thenUpdateReadStatus() {
    // given
    NotificationEntity unreadEntity = NotificationEntity.builder()
        .id(1L)
        .userId(100L)
        .title("Test")
        .message("Message")
        .type("info")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    when(jpaRepository.findById(1L)).thenReturn(Optional.of(unreadEntity));
    when(jpaRepository.save(any(NotificationEntity.class))).thenReturn(unreadEntity);

    // when
    notificationRepository.markAsRead(1L);

    // then
    verify(jpaRepository).findById(1L);
    verify(jpaRepository).save(argThat(entity -> entity.getRead() == true));
  }

  @Test
  void givenNonExistingId_whenMarkAsRead_thenDoNothing() {
    // given
    when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

    // when
    notificationRepository.markAsRead(999L);

    // then
    verify(jpaRepository).findById(999L);
    verify(jpaRepository, never()).save(any());
  }

  @Test
  void givenUserId_whenMarkAllAsReadByUserId_thenCallJpaRepository() {
    // when
    notificationRepository.markAllAsReadByUserId(100L);

    // then
    verify(jpaRepository).markAllAsReadByUserId(100L);
  }

  @Test
  void givenExistingIdAndUserId_whenExistsByIdAndUserId_thenReturnTrue() {
    // given
    when(jpaRepository.existsByIdAndUserId(1L, 100L)).thenReturn(true);

    // when
    boolean result = notificationRepository.existsByIdAndUserId(1L, 100L);

    // then
    assertTrue(result);
    verify(jpaRepository).existsByIdAndUserId(1L, 100L);
  }

  @Test
  void givenNonMatchingIdAndUserId_whenExistsByIdAndUserId_thenReturnFalse() {
    // given
    when(jpaRepository.existsByIdAndUserId(1L, 999L)).thenReturn(false);

    // when
    boolean result = notificationRepository.existsByIdAndUserId(1L, 999L);

    // then
    assertFalse(result);
    verify(jpaRepository).existsByIdAndUserId(1L, 999L);
  }
}
