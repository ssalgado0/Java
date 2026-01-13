package edu.uoc.epcsd.digital.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.digital.domain.DigitalSession;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import edu.uoc.epcsd.digital.domain.exception.DigitalSessionNotEmptyException;
import edu.uoc.epcsd.digital.domain.exception.DigitalSessionNotFoundException;
import edu.uoc.epcsd.digital.domain.exception.UserNotFoundException;
import edu.uoc.epcsd.digital.domain.repository.DigitalItemRepository;
import edu.uoc.epcsd.digital.domain.repository.DigitalSessionRepository;
import edu.uoc.epcsd.digital.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DigitalSessionServiceImplTest {

  @Mock
  private DigitalSessionRepository digitalSessionRepository;

  @Mock
  private DigitalItemRepository digitalItemRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DigitalSessionServiceImpl digitalSessionService;

  @Test
  void findAllDigitalSession_ShouldReturnList() {
    when(digitalSessionRepository.findAllDigitalSession()).thenReturn(Collections.emptyList());
    List<DigitalSession> result = digitalSessionService.findAllDigitalSession();
    assertNotNull(result);
    verify(digitalSessionRepository).findAllDigitalSession();
  }

  @Test
  void findDigitalSessionByUser_ShouldReturnList_WhenUserExists() {
    String email = "test@example.com";
    when(userRepository.findUserByEmail(email)).thenReturn(true);
    when(digitalSessionRepository.findDigitalSessionByUser(email)).thenReturn(
        Collections.emptyList());

    List<DigitalSession> result = digitalSessionService.findDigitalSessionByUser(email);

    assertNotNull(result);
    verify(digitalSessionRepository).findDigitalSessionByUser(email);
  }

  @Test
  void findDigitalSessionByUser_ShouldThrowException_WhenUserNotFound() {
    String email = "test@example.com";
    when(userRepository.findUserByEmail(email)).thenReturn(false);

    assertThrows(UserNotFoundException.class,
        () -> digitalSessionService.findDigitalSessionByUser(email));
  }

  @Test
  void getDigitalSessionById_ShouldReturnOptional() {
    Long id = 1L;
    when(digitalSessionRepository.getDigitalSessionById(id)).thenReturn(Optional.empty());
    Optional<DigitalSession> result = digitalSessionService.getDigitalSessionById(id);
    assertNotNull(result);
    verify(digitalSessionRepository).getDigitalSessionById(id);
  }

  @Test
  void createDigitalSession_ShouldReturnId() {
    DigitalSession session = new DigitalSession();
    when(digitalSessionRepository.createDigitalSession(session)).thenReturn(1L);
    Long result = digitalSessionService.createDigitalSession(session);
    assertEquals(1L, result);
    verify(digitalSessionRepository).createDigitalSession(session);
  }

  @Test
  void updateDigitalSession_ShouldUpdateAndReturnId() {
    Long id = 1L;
    String email = "test@example.com";
    DigitalSession session = new DigitalSession();
    session.setId(id);
    when(digitalSessionRepository.getDigitalSessionById(id)).thenReturn(Optional.of(session));
    when(digitalSessionRepository.updateDigitalSession(any(DigitalSession.class))).thenReturn(id);

    Long result = digitalSessionService.updateDigitalSession(id, email, "Desc");

    assertEquals(id, result);
    assertEquals(email, session.getEmail());
    assertEquals("Desc", session.getDescription());
    assertEquals(DigitalStatus.NOT_AVAILABLE, session.getStatus());
    verify(digitalSessionRepository).updateDigitalSession(session);
  }

  @Test
  void updateDigitalSession_ShouldThrowException_WhenNotFound() {
    Long id = 1L;
    String email = "test@example.com";
    when(digitalSessionRepository.getDigitalSessionById(id)).thenReturn(Optional.empty());
    assertThrows(DigitalSessionNotFoundException.class,
        () -> digitalSessionService.updateDigitalSession(id, email, "Desc"));
  }

  @Test
  void removeDigitalSession_ShouldRemoveSession() {
    Long id = 1L;
    DigitalSession session = new DigitalSession();
    when(digitalSessionRepository.getDigitalSessionById(id)).thenReturn(Optional.of(session));
    when(digitalItemRepository.countDigitalItemBySession(anyLong())).thenReturn(0L);

    digitalSessionService.removeDigitalSession(id);

    verify(digitalSessionRepository).removeDigitalSession(session);
  }

  @Test
  void removeDigitalSession_whenNotEmptySession_ShouldThrowException() {
    Long id = 1L;
    DigitalSession session = new DigitalSession();
    when(digitalSessionRepository.getDigitalSessionById(id)).thenReturn(Optional.of(session));
    when(digitalItemRepository.countDigitalItemBySession(anyLong())).thenReturn(1L);

    DigitalSessionNotEmptyException ex = assertThrows(DigitalSessionNotEmptyException.class,
        () -> digitalSessionService.removeDigitalSession(id));
    assertEquals("Digital session with id 1 is not empty. Cannot be deleted.", ex.getMessage());
  }

  @Test
  void removeDigitalSession_ShouldThrowException_WhenNotFound() {
    Long id = 1L;
    when(digitalSessionRepository.getDigitalSessionById(id)).thenReturn(Optional.empty());
    assertThrows(DigitalSessionNotFoundException.class,
        () -> digitalSessionService.removeDigitalSession(id));
  }
}
