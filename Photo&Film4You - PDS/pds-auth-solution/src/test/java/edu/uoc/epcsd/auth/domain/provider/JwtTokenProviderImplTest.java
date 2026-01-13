package edu.uoc.epcsd.auth.domain.provider;

import static org.assertj.core.api.Assertions.assertThat;

import edu.uoc.epcsd.auth.domain.User;
import edu.uoc.epcsd.auth.domain.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderImplTest {

  private JwtTokenProviderImpl provider;
  private SecretKey key;

  @BeforeEach
  void setUp() {
    byte[] secret = "01234567890123456789012345678901".getBytes(); // 32 bytes
    key = new SecretKeySpec(secret, "HmacSHA256");
    provider = new JwtTokenProviderImpl(key, 3600000L);
  }

  @Test
  void givenUser_whenGenerateToken_thenContainsExpectedClaims() {
    User user = User.builder().id(10L).email("test@test.com").fullName("My Name")
        .role(UserRole.ADMIN).build();

    String token = provider.generateToken(user);
    assertThat(token).isNotBlank().contains(".");

    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

    assertThat(claims.getSubject()).isEqualTo("test@test.com");
    assertThat(claims.getId()).isEqualTo("10");
    assertThat(claims.get("fullName", String.class)).isEqualTo("My Name");
    assertThat(claims.get("role", String.class)).isEqualTo(UserRole.ADMIN.toString());
    assertThat(claims.getExpiration()).isAfter(new Date());
  }
}
