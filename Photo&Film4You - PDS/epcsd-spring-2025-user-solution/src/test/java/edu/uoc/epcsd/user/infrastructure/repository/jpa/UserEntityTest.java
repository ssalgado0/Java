package edu.uoc.epcsd.user.infrastructure.repository.jpa;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.enums.UserRole;
import org.junit.jupiter.api.Test;

class UserEntityTest {

  @Test
  void fromDomain_whenNull() {
    assertNull(UserEntity.fromDomain(null));
  }

  @Test
  void fromDomain() {
    // given
    User user = User.builder()
        .id(10L)
        .fullName("Jane Doe")
        .email("jane.doe@example.com")
        .password("pwd123")
        .phoneNumber("600111222")
        .role(UserRole.USER)
        .build();

    // when
    UserEntity entity = UserEntity.fromDomain(user);

    // then
    assertNotNull(entity);
    assertAll(
        () -> assertEquals(user.getId(), entity.getId()),
        () -> assertEquals(user.getFullName(), entity.getFullName()),
        () -> assertEquals(user.getEmail(), entity.getEmail()),
        () -> assertEquals(user.getPassword(), entity.getPassword()),
        () -> assertEquals(user.getPhoneNumber(), entity.getPhoneNumber()),
        () -> assertEquals(user.getRole(), entity.getRole())
    );
  }

  @Test
  void toDomain() {
    // given
    UserEntity entity = UserEntity.builder()
        .id(20L)
        .fullName("John Smith")
        .email("john.smith@example.com")
        .password("secret")
        .phoneNumber("699888777")
        .role(UserRole.ADMIN)
        .build();

    // when
    User user = entity.toDomain();

    // then
    assertNotNull(user);
    assertAll(
        () -> assertEquals(entity.getId(), user.getId()),
        () -> assertEquals(entity.getFullName(), user.getFullName()),
        () -> assertEquals(entity.getEmail(), user.getEmail()),
        () -> assertEquals(entity.getPassword(), user.getPassword()),
        () -> assertEquals(entity.getPhoneNumber(), user.getPhoneNumber()),
        () -> assertEquals(entity.getRole(), user.getRole())
    );
  }
}