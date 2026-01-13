package edu.uoc.epcsd.auth.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.uoc.epcsd.auth.domain.User;
import edu.uoc.epcsd.auth.domain.enums.UserRole;
import edu.uoc.epcsd.auth.infrastructure.repository.rest.GetUserResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

  private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

  @Test
  void givenGetUserResponse_whenToDomain_thenMapsFields() {
    GetUserResponse resp = GetUserResponse.builder()
        .id(2L)
        .email("x@y.com")
        .fullName("X Y")
        .phoneNumber("000")
        .role(UserRole.ADMIN)
        .build();

    User user = mapper.toDomain(resp);

    assertEquals(2L, user.getId());
    assertEquals("x@y.com", user.getEmail());
    assertEquals(UserRole.ADMIN, user.getRole());
  }
}
