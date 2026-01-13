package edu.uoc.epcsd.productcatalog.testutils;

import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestJwtFactory {

  private final SecretKey signingKey;

  public String generateToken(CurrentUser user) {
    // 1 hour
    long expiration = 3600000;

    return Jwts.builder()
        .id(String.valueOf(user.getId()))
        .subject(user.getEmail())
        .claim("fullName", user.getFullName())
        .claim("role", user.getRole())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(signingKey)
        .compact();
  }
}
