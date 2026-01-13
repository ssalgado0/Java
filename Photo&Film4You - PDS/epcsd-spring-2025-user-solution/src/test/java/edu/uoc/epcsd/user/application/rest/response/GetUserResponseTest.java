package edu.uoc.epcsd.user.application.rest.response;

import static edu.uoc.epcsd.user.domain.enums.UserRole.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;

import edu.uoc.epcsd.user.domain.User;
import org.junit.jupiter.api.Test;

class GetUserResponseTest {

  @Test
  void testFromDomain() {
    User user = User.builder()
        .id(1L)
        .fullName("user")
        .email("test@test.com")
        .phoneNumber("666666666")
        .role(ADMIN)
        .build();
    GetUserResponse response = GetUserResponse.fromDomain(user);
    assertThat(response).isNotNull()
        .extracting("id", "fullName", "email", "phoneNumber", "role")
        .containsExactly(1L, "user", "test@test.com", "666666666", ADMIN);
  }
}