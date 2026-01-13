package edu.uoc.epcsd.user.infrastructure.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  static SecretKey key;
  static JwtTokenProvider provider;

  @BeforeAll
  static void setAllUp() {
    byte[] secret = "01234567890123456789012345678901".getBytes(); // 32 bytes
    key = new SecretKeySpec(secret, "HmacSHA256");
    provider = new JwtTokenProvider(key);
  }

  @Test
  void givenValidToken_whenValidateToken_thenReturnsClaims() {
    String token = generateToken(3600000L); // 1 hour expiration

    var jws = provider.validateToken(token);
    Claims claims = jws.getPayload();

    assertEquals("1", claims.getId());
    assertEquals("test@email.com", claims.getSubject());
    assertEquals("Test User", claims.get("fullName", String.class));
    assertEquals("ADMIN", claims.get("role", String.class));
  }

  @Test
  void givenExpiredToken_whenValidateToken_thenThrowsException() {
    String token = generateToken(-1000L); // Expired 1 second ago
    JwtValidationException exception = assertThrows(JwtValidationException.class, () -> {
      provider.validateToken(token);
    });
    assertEquals("JWT token expired", exception.getMessage());
  }

  @Test
  void givenInvalidToken_whenValidateToken_thenThrowsException() {
    String token = "invalid.token";
    JwtValidationException exception = assertThrows(JwtValidationException.class, () -> {
      provider.validateToken(token);
    });
    assertEquals("Invalid JWT token", exception.getMessage());
  }

  private String generateToken(long expirationMillis) {
    return Jwts.builder()
        .id("1")
        .subject("test@email.com")
        .claim("fullName", "Test User")
        .claim("role", "ADMIN")
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationMillis))
        .signWith(key)
        .compact();
  }
}