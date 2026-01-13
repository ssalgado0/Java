package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.uoc.epcsd.digital.domain.DigitalItem;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import org.junit.jupiter.api.Test;

class DigitalItemEntityTest {

  @Test
  void fromDomain_ShouldMapCorrectly() {
    DigitalItem domain = DigitalItem.builder()
        .id(1L)
        .description("Test Item")
        .lat(10D)
        .lon(20D)
        .link("http://example.com")
        .status(DigitalStatus.AVAILABLE)
        .build();

    DigitalItemEntity entity = DigitalItemEntity.fromDomain(domain);

    assertNotNull(entity);
    assertEquals(domain.getId(), entity.getId());
    assertEquals(domain.getDescription(), entity.getDescription());
    assertEquals(domain.getLat(), entity.getLat());
    assertEquals(domain.getLon(), entity.getLon());
    assertEquals(domain.getLink(), entity.getLink());
    assertEquals(domain.getStatus(), entity.getStatus());
  }

  @Test
  void fromDomain_ShouldReturnNull_WhenInputIsNull() {
    assertNull(DigitalItemEntity.fromDomain(null));
  }

  @Test
  void toDomain_ShouldMapCorrectly() {
    DigitalSessionEntity sessionEntity = new DigitalSessionEntity();
    sessionEntity.setId(100L);

    DigitalItemEntity entity = DigitalItemEntity.builder()
        .id(1L)
        .description("Test Item")
        .lat(10D)
        .lon(20D)
        .link("http://example.com")
        .status(DigitalStatus.AVAILABLE)
        .digitalSession(sessionEntity)
        .build();

    DigitalItem domain = entity.toDomain();

    assertNotNull(domain);
    assertEquals(entity.getId(), domain.getId());
    assertEquals(entity.getDescription(), domain.getDescription());
    assertEquals(entity.getLat(), domain.getLat());
    assertEquals(entity.getLon(), domain.getLon());
    assertEquals(entity.getLink(), domain.getLink());
    assertEquals(entity.getStatus(), domain.getStatus());
    assertEquals(entity.getDigitalSession().getId(), domain.getDigitalsessionid());
  }
}
