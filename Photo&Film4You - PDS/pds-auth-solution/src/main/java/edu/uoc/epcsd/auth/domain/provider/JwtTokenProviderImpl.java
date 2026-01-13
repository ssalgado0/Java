package edu.uoc.epcsd.auth.domain.provider;

import edu.uoc.epcsd.auth.domain.User;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProviderImpl implements TokenProvider {

  private final SecretKey signingKey;
  private final long expiration;

  public JwtTokenProviderImpl(SecretKey signingKey, @Value("${jwt.expiration}") long expiration) {
    this.signingKey = signingKey;
    this.expiration = expiration;
  }

  @Override
  public String generateToken(User user) {
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
