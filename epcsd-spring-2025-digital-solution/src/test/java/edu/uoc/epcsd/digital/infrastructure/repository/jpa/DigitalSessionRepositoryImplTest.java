package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.digital.domain.DigitalSession;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import edu.uoc.epcsd.digital.domain.exception.UserNotFoundException;
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
class DigitalSessionRepositoryImplTest {

  @Mock
  private SpringDataDigitalSessionRepository jpaDigitalSessionRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DigitalSessionRepositoryImpl digitalSessionRepository;

  @Test
  void findAllDigitalSession_ShouldReturnList() {
    DigitalSessionEntity entity = new DigitalSessionEntity(1L, "email@test.com", "Desc",
        DigitalStatus.AVAILABLE);
    when(jpaDigitalSessionRepository.findAll()).thenReturn(Collections.singletonList(entity));

    List<DigitalSession> result = digitalSessionRepository.findAllDigitalSession();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals(entity.getId(), result.get(0).getId());
  }

  @Test
  void getDigitalSessionById_ShouldReturnSession_WhenFound() {
    Long id = 1L;
    DigitalSessionEntity entity = new DigitalSessionEntity(id, "email@test.com", "Desc",
        DigitalStatus.AVAILABLE);
    when(jpaDigitalSessionRepository.getDigitalSessionById(id)).thenReturn(Optional.of(entity));

    Optional<DigitalSession> result = digitalSessionRepository.getDigitalSessionById(id);

    assertTrue(result.isPresent());
    assertEquals(id, result.get().getId());
  }

  @Test
  void createDigitalSession_ShouldReturnId_WhenUserExists() {
    String email = "test@example.com";
    DigitalSession session = DigitalSession.builder().email(email).description("Desc").build();
    DigitalSessionEntity savedEntity = new DigitalSessionEntity();
    savedEntity.setId(10L);

    when(userRepository.findUserByEmail(email)).thenReturn(true);
    when(jpaDigitalSessionRepository.save(any(DigitalSessionEntity.class))).thenReturn(savedEntity);

    Long result = digitalSessionRepository.createDigitalSession(session);

    assertEquals(10L, result);
  }

  @Test
  void createDigitalSession_ShouldThrowException_WhenUserNotFound() {
    String email = "test@example.com";
    DigitalSession session = DigitalSession.builder().email(email).build();

    when(userRepository.findUserByEmail(email)).thenReturn(false);

    assertThrows(UserNotFoundException.class,
        () -> digitalSessionRepository.createDigitalSession(session));
  }

  @Test
  void updateDigitalSession_ShouldReturnId_WhenSuccessful() {
    Long id = 1L;
    String email = "test@example.com";
    DigitalSession session = DigitalSession.builder().id(id).email(email).description("New Desc")
        .build();
    DigitalSessionEntity existingEntity = new DigitalSessionEntity();
    existingEntity.setId(id);

    when(userRepository.findUserByEmail(email)).thenReturn(true);
    when(jpaDigitalSessionRepository.findById(id)).thenReturn(Optional.of(existingEntity));
    when(jpaDigitalSessionRepository.save(any(DigitalSessionEntity.class))).thenReturn(
        existingEntity);

    Long result = digitalSessionRepository.updateDigitalSession(session);

    assertEquals(id, result);
  }

  @Test
  void updateDigitalSession_ShouldThrowException_WhenUserNotFound() {
    Long id = 1L;
    String email = "test@example.com";
    DigitalSession session = DigitalSession.builder().id(id).email(email).description("New Desc")
        .build();

    when(userRepository.findUserByEmail(email)).thenReturn(false);

    assertThrows(UserNotFoundException.class,
        () -> digitalSessionRepository.updateDigitalSession(session));
  }

  @Test
  void updateDigitalSession_ShouldThrowException_WhenSessionNotFound() {
    Long id = 1L;
    String email = "test@example.com";
    DigitalSession session = DigitalSession.builder().id(id).email(email).description("New Desc")
        .build();

    when(userRepository.findUserByEmail(email)).thenReturn(true);
    when(jpaDigitalSessionRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,
        () -> digitalSessionRepository.updateDigitalSession(session));
  }

  @Test
  void removeDigitalSession_ShouldCallDelete() {
    DigitalSession session = DigitalSession.builder().id(1L).build();

    digitalSessionRepository.removeDigitalSession(session);

    verify(jpaDigitalSessionRepository, times(1)).delete(any(DigitalSessionEntity.class));
  }

  @Test
  void existsByIdAndEmail_ShouldReturnTrue_WhenExists() {
    Long id = 1L;
    String email = "test@example.com";

    when(jpaDigitalSessionRepository.existsByIdAndEmail(id, email)).thenReturn(true);

    boolean result = digitalSessionRepository.existsByIdAndEmail(id, email);

    assertTrue(result);
  }

  @Test
  void existsByIdAndEmail_ShouldReturnFalse_WhenNotExists() {
    Long id = 1L;
    String email = "test@example.com";

    when(jpaDigitalSessionRepository.existsByIdAndEmail(id, email)).thenReturn(false);

    boolean result = digitalSessionRepository.existsByIdAndEmail(id, email);

    assertFalse(result);
  }

  @Test
  void existsByIdAndEmail_ShouldReturnFalse_WhenEmailIsNull() {
    Long id = 1L;
    String email = null;

    boolean result = digitalSessionRepository.existsByIdAndEmail(id, email);

    assertFalse(result);
  }

  @Test
  void findDigitalSessionByUser_ShouldReturnList() {
    String email = "test@example.com";
    DigitalSessionEntity entity = new DigitalSessionEntity(1L, email, "Desc",
        DigitalStatus.AVAILABLE);
    when(jpaDigitalSessionRepository.findDigitalSessionByUser(email)).thenReturn(
        Collections.singletonList(entity));

    List<DigitalSession> result = digitalSessionRepository.findDigitalSessionByUser(email);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals(entity.getId(), result.get(0).getId());
    assertEquals(entity.getEmail(), result.get(0).getEmail());
  }
}
