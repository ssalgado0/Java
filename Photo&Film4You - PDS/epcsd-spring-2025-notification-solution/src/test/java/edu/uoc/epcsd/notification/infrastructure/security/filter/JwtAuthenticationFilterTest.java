package edu.uoc.epcsd.notification.infrastructure.security.filter;

import edu.uoc.epcsd.notification.infrastructure.security.jwt.JwtTokenProvider;
import edu.uoc.epcsd.notification.infrastructure.security.jwt.JwtValidationException;
import edu.uoc.epcsd.notification.infrastructure.security.model.CurrentUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtTokenProvider tokenProvider;

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private MockFilterChain filterChain;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    filterChain = new MockFilterChain();
    SecurityContextHolder.clearContext();
  }

  @Test
  void givenValidBearerToken_whenDoFilterInternal_thenSetAuthentication() throws ServletException, IOException {
    // given
    String token = "valid.jwt.token";
    request.addHeader("Authorization", "Bearer " + token);

    // Create a mock Jws<Claims> with proper Claims
    @SuppressWarnings("unchecked")
    Jws<Claims> jws = mock(Jws.class);
    Claims claims = mock(Claims.class);
    
    when(jws.getPayload()).thenReturn(claims);
    when(claims.getId()).thenReturn("123");
    when(claims.getSubject()).thenReturn("user@test.com");
    when(claims.get("fullName", String.class)).thenReturn("Test User");
    when(claims.get("role", String.class)).thenReturn("USER");
    
    when(tokenProvider.validateToken(token)).thenReturn(jws);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertTrue(authentication.getPrincipal() instanceof CurrentUser);
    
    CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    assertEquals("123", currentUser.getId());
    assertEquals("user@test.com", currentUser.getEmail());
    assertEquals("Test User", currentUser.getFullName());
    assertEquals("USER", currentUser.getRole());
    
    assertEquals(1, authentication.getAuthorities().size());
    assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
    
    verify(tokenProvider).validateToken(token);
  }

  @Test
  void givenNoAuthorizationHeader_whenDoFilterInternal_thenDoNotSetAuthentication() throws ServletException, IOException {
    // given - no header set

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNull(authentication);
    verify(tokenProvider, never()).validateToken(anyString());
  }

  @Test
  void givenNonBearerToken_whenDoFilterInternal_thenDoNotSetAuthentication() throws ServletException, IOException {
    // given
    request.addHeader("Authorization", "Basic someToken");

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNull(authentication);
    verify(tokenProvider, never()).validateToken(anyString());
  }

  @Test
  void givenInvalidToken_whenDoFilterInternal_thenThrowException() {
    // given
    String token = "invalid.jwt.token";
    request.addHeader("Authorization", "Bearer " + token);
    
    when(tokenProvider.validateToken(token)).thenThrow(new JwtValidationException("Invalid JWT token"));

    // when & then
    assertThrows(JwtValidationException.class, 
        () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
    
    verify(tokenProvider).validateToken(token);
  }
}
