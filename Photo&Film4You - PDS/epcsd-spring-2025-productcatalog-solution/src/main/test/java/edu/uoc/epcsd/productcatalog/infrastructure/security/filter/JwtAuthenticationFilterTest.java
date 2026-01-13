// java
package edu.uoc.epcsd.productcatalog.infrastructure.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.productcatalog.infrastructure.security.jwt.JwtTokenProvider;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private Jws<Claims> parsed;

  private JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(tokenProvider);
    SecurityContextHolder.clearContext();
  }

  @Test
  void whenValidToken_setsAuthentication() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer faketoken");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    Claims claims = mock(Claims.class);
    when(tokenProvider.validateToken("faketoken")).thenReturn(parsed);
    when(parsed.getPayload()).thenReturn(claims);
    when(claims.getId()).thenReturn("123");
    when(claims.getSubject()).thenReturn("user@example.com");
    when(claims.get("fullName", String.class)).thenReturn("John Doe");
    when(claims.get("role", String.class)).thenReturn("USER");

    filter.doFilterInternal(request, response, chain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth, "Authentication should be set");
    assertTrue(auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")), "Authority ROLE_USER expected");
    assertInstanceOf(CurrentUser.class, auth.getPrincipal(), "Principal must be CurrentUser");

    CurrentUser principal = (CurrentUser) auth.getPrincipal();
    assertEquals("123", principal.getId());
    assertEquals("user@example.com", principal.getEmail()); // ajuste si el getter tiene otro nombre
    assertEquals("John Doe", principal.getFullName());
    assertEquals("USER", principal.getRole());
  }

  @Test
  void whenNoAuthorizationHeader_doesNotSetAuthentication() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilterInternal(request, response, chain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth, "Authentication should not be set when no Authorization header");
  }
}
