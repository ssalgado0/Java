package edu.uoc.epcsd.digital.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.uoc.epcsd.digital.domain.DigitalItem;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import edu.uoc.epcsd.digital.domain.exception.DigitalSessionNotFoundException;
import edu.uoc.epcsd.digital.domain.repository.DigitalItemRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import edu.uoc.epcsd.digital.infrastructure.kafka.DigitalItemMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DigitalItemServiceImplTest {

  @Mock
  private DigitalItemRepository digitalItemRepository;

  @Mock
  private KafkaTemplate<String, DigitalItemMessage> digitalItemKafkaTemplate;

  @InjectMocks
  private DigitalItemServiceImpl digitalItemService;

  @Test
  void findAllDigitalItem_ShouldReturnList() {
    when(digitalItemRepository.findAllDigitalItem()).thenReturn(Collections.emptyList());
    List<DigitalItem> result = digitalItemService.findAllDigitalItem();
    assertNotNull(result);
    verify(digitalItemRepository).findAllDigitalItem();
  }

  @Test
  void findDigitalItemBySession_ShouldReturnList() {
    Long sessionId = 1L;
    when(digitalItemRepository.findDigitalItemBySession(sessionId)).thenReturn(
        Collections.emptyList());
    List<DigitalItem> result = digitalItemService.findDigitalItemBySession(sessionId);
    assertNotNull(result);
    verify(digitalItemRepository).findDigitalItemBySession(sessionId);
  }

  @Test
  void getDigitalItemById_ShouldReturnOptional() {
    Long id = 1L;
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.empty());
    Optional<DigitalItem> result = digitalItemService.getDigitalItemById(id);
    assertNotNull(result);
    verify(digitalItemRepository).getDigitalItemById(id);
  }

  @Test
  void addDigitalItem_ShouldReturnId() {
    DigitalItem item = new DigitalItem();
    when(digitalItemRepository.createDigitalItem(item)).thenReturn(1L);
    Long result = digitalItemService.addDigitalItem(item);
    assertEquals(1L, result);
    verify(digitalItemRepository).createDigitalItem(item);
  }

  @Test
  void updateDigitalItem_ShouldUpdateAndReturnId() {
    Long id = 1L;
    DigitalItem item = new DigitalItem();
    item.setId(id);
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(item));
    when(digitalItemRepository.updateDigitalItem(any(DigitalItem.class))).thenReturn(id);

    Long result = digitalItemService.updateDigitalItem(id, "Desc", "Link", 10D, 20D);

    assertEquals(id, result);
    assertEquals("Desc", item.getDescription());
    assertEquals("Link", item.getLink());
    assertEquals(10L, item.getLat());
    assertEquals(20L, item.getLon());
    verify(digitalItemRepository).updateDigitalItem(item);
  }

  @Test
  void updateDigitalItem_ShouldThrowException_WhenNotFound() {
    Long id = 1L;
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.empty());
    assertThrows(DigitalSessionNotFoundException.class,
        () -> digitalItemService.updateDigitalItem(id, "Desc", "Link", 10D, 20D));
  }

  @Test
  void dropDigitalItem_ShouldRemoveItem() {
    Long id = 1L;
    DigitalItem item = new DigitalItem();
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(item));

    digitalItemService.dropDigitalItem(id);

    verify(digitalItemRepository).removeDigitalItem(item);
  }

  @Test
  void dropDigitalItem_ShouldThrowException_WhenNotFound() {
    Long id = 1L;
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.empty());
    assertThrows(DigitalSessionNotFoundException.class,
        () -> digitalItemService.dropDigitalItem(id));
  }

  @Test
  void setDigitalItemForReview_ShouldSetStatusToReviewPending() {
    Long id = 1L;
    DigitalItem item = new DigitalItem();
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(item));

    digitalItemService.setDigitalItemForReview(id);

    assertEquals(DigitalStatus.REVIEW_PENDING, item.getStatus());
    verify(digitalItemRepository).updateDigitalItem(item);
    verify(digitalItemKafkaTemplate, times(1)).send(anyString(), any(DigitalItemMessage.class));
  }

  @Test
  void approvePendingDigitalItem_ShouldSetStatusToAvailable() {
    Long id = 1L;
    DigitalItem item = new DigitalItem();
    item.setStatus(DigitalStatus.REVIEW_PENDING);
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(item));

    digitalItemService.approvePendingDigitalItem(id);

    assertEquals(DigitalStatus.AVAILABLE, item.getStatus());
    verify(digitalItemRepository).updateDigitalItem(item);
  }

  @Test
  void approvePendingDigitalItem_ShouldThrowException_WhenStatusNotReviewPending() {
    Long id = 1L;
    DigitalItem item = new DigitalItem();
    item.setStatus(DigitalStatus.AVAILABLE);
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(item));

    assertThrows(ResponseStatusException.class,
        () -> digitalItemService.approvePendingDigitalItem(id));
  }

  @Test
  void rejectPendingDigitalItem_ShouldSetStatusToNotAvailable() {
    Long id = 1L;
    DigitalItem item = new DigitalItem();
    item.setStatus(DigitalStatus.REVIEW_PENDING);
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(item));

    digitalItemService.rejectPendingDigitalItem(id);

    assertEquals(DigitalStatus.NOT_AVAILABLE, item.getStatus());
    verify(digitalItemRepository).updateDigitalItem(item);
  }

  @Test
  void rejectPendingDigitalItem_ShouldThrowException_WhenStatusNotReviewPending() {
    Long id = 1L;
    DigitalItem item = new DigitalItem();
    item.setStatus(DigitalStatus.AVAILABLE);
    when(digitalItemRepository.getDigitalItemById(id)).thenReturn(Optional.of(item));

    assertThrows(ResponseStatusException.class,
        () -> digitalItemService.rejectPendingDigitalItem(id));
  }
}
