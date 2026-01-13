package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.uoc.epcsd.digital.domain.DigitalSession;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import org.junit.jupiter.api.Test;

class DigitalSessionEntityTest {

  @Test
  void fromDomain_ShouldMapCorrectly() {
    DigitalSession domain = DigitalSession.builder()
        .id(1L)
        .email("test@example.com")
        .description("Test Session")
        .status(DigitalStatus.AVAILABLE)
        .build();

    DigitalSessionEntity entity = DigitalSessionEntity.fromDomain(domain);

    assertNotNull(entity);
    assertEquals(domain.getId(), entity.getId());
    assertEquals(domain.getEmail(), entity.getEmail());
    assertEquals(domain.getDescription(), entity.getDescription());
    assertEquals(domain.getStatus(), entity.getStatus());
  }

  @Test
  void fromDomain_ShouldReturnNull_WhenInputIsNull() {
    assertNull(DigitalSessionEntity.fromDomain(null));
  }

  @Test
  void toDomain_ShouldMapCorrectly() {
    DigitalSessionEntity entity = DigitalSessionEntity.builder()
        .id(1L)
        .email("test@example.com")
        .description("Test Session")
        .status(DigitalStatus.AVAILABLE)
        .build();

    DigitalSession domain = entity.toDomain();

    assertNotNull(domain);
    assertEquals(entity.getId(), domain.getId());
    assertEquals(entity.getEmail(), domain.getEmail());
    assertEquals(entity.getDescription(), domain.getDescription());
    assertEquals(entity.getStatus(), domain.getStatus());
  }
}
