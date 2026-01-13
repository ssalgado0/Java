package edu.uoc.epcsd.notification.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final SecretKey key;

  public Jws<Claims> validateToken(String token) {
    try {
      JwtParser parser = Jwts.parser()
          .verifyWith(key)
          .build();

      return parser.parseSignedClaims(token);

    } catch (ExpiredJwtException e) {
      throw new JwtValidationException("JWT token expired", e);
    } catch (JwtException | IllegalArgumentException e) {
      throw new JwtValidationException("Invalid JWT token", e);
    }
  }
}
