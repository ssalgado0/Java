package edu.uoc.epcsd.notification.infrastructure.repository.jpa;

import edu.uoc.epcsd.notification.domain.Notification;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationEntityTest {

  @Test
  void givenNotification_whenFromDomain_thenReturnEntity() {
    // given
    Notification notification = Notification.builder()
        .id(1L)
        .userId(100L)
        .title("Test Title")
        .message("Test Message")
        .type("info")
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .read(false).entity(Notification.EntityReference.GENERAL)
        .build();

    // when
    NotificationEntity entity = NotificationEntity.fromDomain(notification);

    // then
    assertNotNull(entity);
    assertEquals(1L, entity.getId());
    assertEquals(100L, entity.getUserId());
    assertEquals("Test Title", entity.getTitle());
    assertEquals("Test Message", entity.getMessage());
    assertEquals("info", entity.getType());
    assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), entity.getCreatedAt());
    assertFalse(entity.getRead());
  }

  @Test
  void givenNullNotification_whenFromDomain_thenReturnNull() {
    // when
    NotificationEntity entity = NotificationEntity.fromDomain(null);

    // then
    assertNull(entity);
  }

  @Test
  void givenEntity_whenToDomain_thenReturnNotification() {
    // given
    NotificationEntity entity = NotificationEntity.builder()
        .id(2L)
        .userId(200L)
        .title("Entity Title")
        .message("Entity Message")
        .type("warning")
        .createdAt(LocalDateTime.of(2025, 2, 1, 15, 30))
        .read(true).entityReference("GENERAL")
        .build();

    // when
    Notification notification = entity.toDomain();

    // then
    assertNotNull(notification);
    assertEquals(2L, notification.getId());
    assertEquals(200L, notification.getUserId());
    assertEquals("Entity Title", notification.getTitle());
    assertEquals("Entity Message", notification.getMessage());
    assertEquals("warning", notification.getType());
    assertEquals(LocalDateTime.of(2025, 2, 1, 15, 30), notification.getCreatedAt());
    assertTrue(notification.getRead());
  }

  @Test
  void givenTwoEntitiesWithSameData_whenEquals_thenReturnTrue() {
    // given
    NotificationEntity entity1 = NotificationEntity.builder()
        .id(1L)
        .userId(100L)
        .title("Title")
        .message("Message")
        .type("info")
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .read(false)
        .build();

    NotificationEntity entity2 = NotificationEntity.builder()
        .id(1L)
        .userId(100L)
        .title("Title")
        .message("Message")
        .type("info")
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .read(false)
        .build();

    // when & then
    assertEquals(entity1, entity2);
    assertEquals(entity1.hashCode(), entity2.hashCode());
  }
}
