package edu.uoc.epcsd.user.domain.service.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserInternalServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserInternalServiceImpl userInternalService;

  @Test
  void givenExistingEmailAndCorrectPassword_whenGetUser_thenReturnUser() {
    // given
    String email = "test@example.com";
    String rawPassword = "secret";
    User user = User.builder()
        .id(1L)
        .fullName("Test User")
        .email(email)
        .password("encoded")
        .phoneNumber("123456789")
        .build();

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);

    // when
    Optional<User> result = userInternalService.getUser(email, rawPassword);

    // then
    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    verify(userRepository).findUserByEmail(email);
    verify(passwordEncoder).matches(rawPassword, user.getPassword());
  }

  @Test
  void givenExistingEmailAndWrongPassword_whenGetUser_thenReturnEmpty() {
    // given
    String email = "test2@example.com";
    String rawPassword = "wrong";
    User user = User.builder()
        .id(2L)
        .fullName("Other User")
        .email(email)
        .password("encoded2")
        .phoneNumber("987654321")
        .build();

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

    // when
    Optional<User> result = userInternalService.getUser(email, rawPassword);

    // then
    assertFalse(result.isPresent());
    verify(userRepository).findUserByEmail(email);
    verify(passwordEncoder).matches(rawPassword, user.getPassword());
  }

  @Test
  void givenNonExistingEmail_whenGetUser_thenReturnEmpty() {
    // given
    String email = "missing@example.com";
    String rawPassword = "any";

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

    // when
    Optional<User> result = userInternalService.getUser(email, rawPassword);

    // then
    assertFalse(result.isPresent());
    verify(userRepository).findUserByEmail(email);
    verify(passwordEncoder, never()).matches(anyString(), anyString());
  }
}