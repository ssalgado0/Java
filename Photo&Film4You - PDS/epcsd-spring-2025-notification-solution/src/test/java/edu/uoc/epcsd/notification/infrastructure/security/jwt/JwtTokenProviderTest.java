package edu.uoc.epcsd.notification.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;
  private SecretKey secretKey;

  @BeforeEach
  void setUp() {
    // Create a test secret key
    byte[] keyBytes = "testSecretKeyForJwtValidation1234567890".getBytes();
    secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    jwtTokenProvider = new JwtTokenProvider(secretKey);
  }

  @Test
  void givenValidToken_whenValidateToken_thenReturnClaims() {
    // given
    String token = Jwts.builder()
        .id("123")
        .subject("user@test.com")
        .claim("fullName", "Test User")
        .claim("role", "USER")
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
        .signWith(secretKey)
        .compact();

    // when
    Jws<Claims> result = jwtTokenProvider.validateToken(token);

    // then
    assertNotNull(result);
    assertEquals("123", result.getPayload().getId());
    assertEquals("user@test.com", result.getPayload().getSubject());
    assertEquals("Test User", result.getPayload().get("fullName", String.class));
    assertEquals("USER", result.getPayload().get("role", String.class));
  }

  @Test
  void givenExpiredToken_whenValidateToken_thenThrowJwtValidationException() {
    // given
    String expiredToken = Jwts.builder()
        .subject("user@test.com")
        .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
        .expiration(new Date(System.currentTimeMillis() - 3600000)) // expired 1 hour ago
        .signWith(secretKey)
        .compact();

    // when & then
    JwtValidationException exception = assertThrows(JwtValidationException.class, 
        () -> jwtTokenProvider.validateToken(expiredToken));
    
    assertEquals("JWT token expired", exception.getMessage());
    assertTrue(exception.getCause() instanceof ExpiredJwtException);
  }

  @Test
  void givenInvalidToken_whenValidateToken_thenThrowJwtValidationException() {
    // given
    String invalidToken = "invalid.jwt.token";

    // when & then
    JwtValidationException exception = assertThrows(JwtValidationException.class, 
        () -> jwtTokenProvider.validateToken(invalidToken));
    
    assertEquals("Invalid JWT token", exception.getMessage());
  }

  @Test
  void givenNullToken_whenValidateToken_thenThrowJwtValidationException() {
    // given
    String nullToken = null;

    // when & then
    JwtValidationException exception = assertThrows(JwtValidationException.class, 
        () -> jwtTokenProvider.validateToken(nullToken));
    
    assertEquals("Invalid JWT token", exception.getMessage());
  }
}
