package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.digital.domain.DigitalItem;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DigitalItemRepositoryImplTest {

  @Mock
  private SpringDataDigitalItemRepository jpaDigitalItemRepository;

  @Mock
  private SpringDataDigitalSessionRepository jpaDigitalSessionRepository;

  @InjectMocks
  private DigitalItemRepositoryImpl digitalItemRepository;

  @Test
  void findAllDigitalItem_ShouldReturnListOfDigitalItems() {
    DigitalSessionEntity sessionEntity = new DigitalSessionEntity();
    sessionEntity.setId(1L);
    DigitalItemEntity entity = new DigitalItemEntity(1L, "Desc", 10D, 20D, "Link",
        DigitalStatus.AVAILABLE, sessionEntity);
    when(jpaDigitalItemRepository.findAll()).thenReturn(Collections.singletonList(entity));

    List<DigitalItem> result = digitalItemRepository.findAllDigitalItem();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals(entity.getId(), result.get(0).getId());
  }

  @Test
  void getDigitalItemById_ShouldReturnDigitalItem_WhenFound() {
    Long id = 1L;
    DigitalSessionEntity sessionEntity = new DigitalSessionEntity();
    sessionEntity.setId(1L);
    DigitalItemEntity entity = new DigitalItemEntity(id, "Desc", 10D, 20D, "Link",
        DigitalStatus.AVAILABLE, sessionEntity);
    when(jpaDigitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(entity));

    Optional<DigitalItem> result = digitalItemRepository.getDigitalItemById(id);

    assertTrue(result.isPresent());
    assertEquals(id, result.get().getId());
  }

  @Test
  void createDigitalItem_ShouldReturnId_WhenSuccessful() {
    Long sessionId = 1L;
    DigitalItem digitalItem = DigitalItem.builder().digitalsessionid(sessionId).description("Desc")
        .build();
    DigitalSessionEntity sessionEntity = new DigitalSessionEntity();
    sessionEntity.setId(sessionId);
    DigitalItemEntity savedEntity = new DigitalItemEntity();
    savedEntity.setId(10L);

    when(jpaDigitalSessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionEntity));
    when(jpaDigitalItemRepository.save(any(DigitalItemEntity.class))).thenReturn(savedEntity);

    Long result = digitalItemRepository.createDigitalItem(digitalItem);

    assertEquals(10L, result);
  }

  @Test
  void updateDigitalItem_ShouldReturnId_WhenSuccessful() {
    Long itemId = 1L;
    Long sessionId = 2L;
    DigitalItem digitalItem = DigitalItem.builder().id(itemId).digitalsessionid(sessionId)
        .description("New Desc").build();
    DigitalItemEntity existingEntity = new DigitalItemEntity();
    existingEntity.setId(itemId);
    DigitalSessionEntity sessionEntity = new DigitalSessionEntity();
    sessionEntity.setId(sessionId);

    when(jpaDigitalItemRepository.findById(itemId)).thenReturn(Optional.of(existingEntity));
    when(jpaDigitalSessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionEntity));
    when(jpaDigitalItemRepository.save(any(DigitalItemEntity.class))).thenReturn(existingEntity);

    Long result = digitalItemRepository.updateDigitalItem(digitalItem);

    assertEquals(itemId, result);
  }

  @Test
  void removeDigitalItem_ShouldCallDelete() {
    DigitalItem digitalItem = DigitalItem.builder().id(1L).build();

    digitalItemRepository.removeDigitalItem(digitalItem);

    verify(jpaDigitalItemRepository, times(1)).delete(any(DigitalItemEntity.class));
  }

  @Test
  void existsByIdAndEmail_ShouldReturnFalse() {
    Long id = 1L;
    String email = "test@example.com";

    boolean result = digitalItemRepository.existsByIdAndEmail(id, email);

    assertFalse(result);
  }

  @Test
  void findDigitalItemBySession_ShouldReturnList() {
    Long sessionId = 1L;
    DigitalSessionEntity sessionEntity = new DigitalSessionEntity();
    sessionEntity.setId(sessionId);
    DigitalItemEntity entity = new DigitalItemEntity(1L, "Desc", 10D, 20D, "Link",
        DigitalStatus.AVAILABLE, sessionEntity);

    when(jpaDigitalItemRepository.findDigitalItemByDigitalSession(sessionId)).thenReturn(
        Collections.singletonList(entity));

    List<DigitalItem> result = digitalItemRepository.findDigitalItemBySession(sessionId);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals(entity.getId(), result.get(0).getId());
    assertEquals(sessionId, result.get(0).getDigitalsessionid());
  }
}
