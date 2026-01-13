package edu.uoc.epcsd.auth.infrastructure.repository.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.auth.application.rest.request.LoginRequest;
import edu.uoc.epcsd.auth.domain.User;
import edu.uoc.epcsd.auth.domain.enums.UserRole;
import edu.uoc.epcsd.auth.domain.mapper.UserMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class UserRepositoryImplTest {

  private RestTemplate restTemplate;
  private UserMapper userMapper;
  private UserRepositoryImpl repo;

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    userMapper = mock(UserMapper.class);
    repo = new UserRepositoryImpl(restTemplate, userMapper, "http://users/login");
  }

  @Test
  void given200AndBody_whenGetLoginUser_thenReturnsMappedUser() {
    GetUserResponse response = GetUserResponse.builder()
        .id(1L)
        .email("test@test.com")
        .fullName("Name")
        .phoneNumber("123")
        .role(UserRole.USER)
        .build();

    when(restTemplate.postForEntity(eq("http://users/login"), any(LoginRequest.class),
        eq(GetUserResponse.class)))
        .thenReturn(ResponseEntity.ok(response));
    when(userMapper.toDomain(response)).thenReturn(
        User.builder().id(1L).email("test@test.com").build());

    Optional<User> user = repo.getLoginUser("test@test.com", "pwd");

    assertTrue(user.isPresent());
    assertEquals(1L, user.get().getId());
  }

  @Test
  void givenNon200_whenGetLoginUser_thenReturnsEmpty() {
    when(restTemplate.postForEntity(eq("http://users/login"), any(LoginRequest.class),
        eq(GetUserResponse.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

    Optional<User> user = repo.getLoginUser("test@test.com", "pwd");

    assertFalse(user.isPresent());
  }
}
