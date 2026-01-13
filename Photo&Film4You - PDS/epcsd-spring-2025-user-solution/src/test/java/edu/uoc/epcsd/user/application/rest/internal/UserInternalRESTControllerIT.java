package edu.uoc.epcsd.user.application.rest.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import edu.uoc.epcsd.user.application.rest.request.LoginRequest;
import edu.uoc.epcsd.user.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.user.domain.enums.UserRole;
import edu.uoc.epcsd.user.infrastructure.repository.jpa.SpringDataUserRepository;
import edu.uoc.epcsd.user.infrastructure.repository.jpa.UserEntity;
import edu.uoc.epcsd.user.testutils.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserInternalRESTControllerIT extends IntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private SpringDataUserRepository springDataUserRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  private UserEntity user;

  @BeforeEach
  void setUp() {
    user = springDataUserRepository.saveAndFlush(UserEntity.builder()
        .email("test@test.com")
        .fullName("test")
        .password(passwordEncoder.encode("password"))
        .role(UserRole.ADMIN)
        .phoneNumber("666666666")
        .build());
  }

  @AfterEach
  void tearDown() {
    springDataUserRepository.deleteAll();
  }

  @Test
  void givenValidEmailAndPassword_whenGetUser_thenReturnUser() {
    LoginRequest request = LoginRequest.builder()
        .email("test@test.com")
        .password("password")
        .build();

    ResponseEntity<GetUserResponse> response = assertDoesNotThrow(() ->
        restTemplate.postForEntity(baseUrl() + "/internal/users/login", request,
            GetUserResponse.class));

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull()
        .extracting("id", "fullName", "email", "role", "phoneNumber")
        .contains(user.getId(), user.getFullName(), user.getEmail(), user.getRole(),
            user.getPhoneNumber());
  }

  @Test
  void givenInvalidPassword_whenGetUser_thenReturnNotFound() {
    LoginRequest request = LoginRequest.builder()
        .email("test@test.com")
        .password("wrongpassword")
        .build();

    ResponseEntity<GetUserResponse> response = assertDoesNotThrow(() ->
        restTemplate.postForEntity(baseUrl() + "/internal/users/login", request,
            GetUserResponse.class));

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNull();
  }

  @Test
  void givenValidEmail_whenGetUserByEmail_thenReturnUser() {
    ResponseEntity<GetUserResponse> response = assertDoesNotThrow(() ->
        restTemplate.getForEntity(baseUrl() + "/internal/users/byEmail/test@test.com",
            GetUserResponse.class));

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull()
        .extracting("id", "fullName", "email", "role", "phoneNumber")
        .contains(user.getId(), user.getFullName(), user.getEmail(), user.getRole(),
            user.getPhoneNumber());
  }

  @Test
  void givençInvalidEmail_whenGetUserByEmail_thenNotFound() {
    ResponseEntity<GetUserResponse> response = assertDoesNotThrow(() ->
        restTemplate.getForEntity(baseUrl() + "/internal/users/byEmail/invalid@email.com",
            GetUserResponse.class));

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNull();
  }
}