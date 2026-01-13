package edu.uoc.epcsd.auth.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.auth.domain.User;
import edu.uoc.epcsd.auth.domain.enums.UserRole;
import edu.uoc.epcsd.auth.domain.provider.TokenProvider;
import edu.uoc.epcsd.auth.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  private final String email = "user@example.com";
  private final String password = "pass";
  private final User user = User.builder()
      .id(1L)
      .email(email)
      .fullName("User Test")
      .role(UserRole.USER)
      .build();
  @Mock
  private UserRepository userRepository;
  @Mock
  private TokenProvider tokenProvider;
  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void givenUserExists_whenGenerateToken_thenReturnsToken() {
    when(userRepository.getLoginUser(email, password)).thenReturn(Optional.of(user));
    when(tokenProvider.generateToken(user)).thenReturn("jwt-token");

    Optional<String> result = authService.generateToken(email, password);

    assertTrue(result.isPresent());
    assertEquals("jwt-token", result.get());
  }

  @Test
  void givenUserNotFound_whenGenerateToken_thenReturnsEmpty() {
    when(userRepository.getLoginUser(email, password)).thenReturn(Optional.empty());

    Optional<String> result = authService.generateToken(email, password);

    assertFalse(result.isPresent());
  }
}
