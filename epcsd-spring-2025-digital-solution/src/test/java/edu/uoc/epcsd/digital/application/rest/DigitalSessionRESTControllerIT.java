package edu.uoc.epcsd.digital.application.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.uoc.epcsd.digital.application.rest.request.CreateDigitalSessionRequest;
import edu.uoc.epcsd.digital.domain.DigitalSession;
import edu.uoc.epcsd.digital.infrastructure.repository.rest.GetUserResponse;
import edu.uoc.epcsd.digital.infrastructure.security.model.CurrentUser;
import edu.uoc.epcsd.digital.testutils.IntegrationTest;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class DigitalSessionRESTControllerIT extends IntegrationTest {

  private CurrentUser currentUser;

  static Stream<Arguments> anonymousRequests() {
    return Stream.of(
        arguments("/digital/allDigital", HttpMethod.GET, new HttpEntity<>(null)),
        arguments("/digital/1", HttpMethod.GET, new HttpEntity<>(null)),
        arguments("/digital", HttpMethod.GET, new HttpEntity<>(null)),
        arguments("/digital/digitalByUser?email=juanpa@gmail.com", HttpMethod.GET,
            new HttpEntity<>(null)),
        arguments("/digital/createDigital", HttpMethod.POST,
            new HttpEntity<>(new CreateDigitalSessionRequest("juanpa@gmail.com", "test"))),
        arguments("/digital/updateDigital/1", HttpMethod.PUT,
            new HttpEntity<>(new CreateDigitalSessionRequest("juanpa@gmail.com", "test"))),
        arguments("/digital/removeDigital/1", HttpMethod.DELETE, new HttpEntity<>(null)));
  }

  @BeforeEach
  void setUp() {
    currentUser = CurrentUser.builder()
        .id("1")
        .email("juanpa@gmail.com")
        .fullName("Test User")
        .role("USER")
        .build();
  }

  @ParameterizedTest(name = "Anonymous requesting {1} {0} should return forbidden")
  @MethodSource("anonymousRequests")
  void givenAnonymous_thenForbidden(String endpoint, HttpMethod method,
      HttpEntity<Object> request) {
    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + endpoint,
        method,
        request,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenInvalidCreateDigitalSessionRequest_whenCreateDigitalSession_thenBadRequest() {
    CreateDigitalSessionRequest request = new CreateDigitalSessionRequest("", "");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));

    HttpEntity<CreateDigitalSessionRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Object> response = restTemplate.postForEntity(
        baseUrl() + "/digital/createDigital",
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void givenAdminUser_whenGetAllDigitalSession_thenOk() {
    CurrentUser adminUser = CurrentUser.builder()
        .id("2")
        .email("admin@test.com")
        .role("ADMIN")
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(adminUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<List<DigitalSession>> response = restTemplate.exchange(
        baseUrl() + "/digital/allDigital",
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        });

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().size() >= 5);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "/digital/allDigital",
      "/digital/2",
      "/digital/digitalByUser?email=juanpa@gmail.com"
  })
  void givenUser_whenAccessForbiddenEndpoint_thenForbidden(String endpoint) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + endpoint,
        HttpMethod.GET,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenUserWithAccess_whenGetDigitalSessionById_thenOk() {
    // juanpa@gmail.com has access to session 1
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<DigitalSession> response = restTemplate.exchange(
        baseUrl() + "/digital/1",
        HttpMethod.GET,
        entity,
        DigitalSession.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
  }

  @Test
  void givenAdminUser_whenGetDigitalSessionById_thenOk() {
    CurrentUser adminUser = CurrentUser.builder()
        .id("2")
        .email("admin@test.com")
        .role("ADMIN")
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(adminUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<DigitalSession> response = restTemplate.exchange(
        baseUrl() + "/digital/1",
        HttpMethod.GET,
        entity,
        DigitalSession.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
  }

  @Test
  void givenUser_whenFindDigitalSessionByUser_thenOk() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<List<DigitalSession>> response = restTemplate.exchange(
        baseUrl() + "/digital",
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        });

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody())
        .isNotNull()
        .hasSize(2)
        .extracting(DigitalSession::getEmail)
        .contains("juanpa@gmail.com");
  }

  @Test
  void givenAdminUser_whenFindDigitalSessionByUserEmail_thenOk() throws JsonProcessingException {
    GetUserResponse userResponse = GetUserResponse.builder()
        .email("juanpa@gmail.com")
        .build();

    userResponseBody = objectMapper.writeValueAsString(userResponse);

    CurrentUser adminUser = CurrentUser.builder()
        .id("2")
        .email("admin@test.com")
        .role("ADMIN")
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(adminUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<List<DigitalSession>> response = restTemplate.exchange(
        baseUrl() + "/digital/digitalByUser?email=juanpa@gmail.com",
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        });

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody())
        .isNotNull()
        .hasSize(2)
        .extracting(DigitalSession::getEmail)
        .contains("juanpa@gmail.com");
  }

  @Test
  void givenUserCreatingOwnSession_whenCreateDigitalSession_thenCreated() {
    CreateDigitalSessionRequest request = new CreateDigitalSessionRequest("juanpa@gmail.com",
        "New Session");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalSessionRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Long> response = restTemplate.postForEntity(
        baseUrl() + "/digital/createDigital",
        entity,
        Long.class);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void givenUserCreatingOtherUserSession_whenCreateDigitalSession_thenForbidden() {
    CreateDigitalSessionRequest request = new CreateDigitalSessionRequest("other@gmail.com",
        "New Session");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalSessionRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Object> response = restTemplate.postForEntity(
        baseUrl() + "/digital/createDigital",
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenUserUpdatingOwnSession_whenUpdateDigitalSession_thenOk() {
    // juanpa@gmail.com owns session 1
    CreateDigitalSessionRequest request = new CreateDigitalSessionRequest("juanpa@gmail.com",
        "Updated Description");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalSessionRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digital/updateDigital/1",
        HttpMethod.PUT,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Boolean.TRUE, response.getBody());
  }

  @Test
  void givenUserUpdatingOtherUserSession_whenUpdateDigitalSession_thenForbidden() {
    // juanpa@gmail.com does not own session 2
    CreateDigitalSessionRequest request = new CreateDigitalSessionRequest("juanpa@gmail.com",
        "Updated Description");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalSessionRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digital/updateDigital/2",
        HttpMethod.PUT,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenUserRemovingOwnSession_whenRemoveDigitalSessionAndIsNotEmpty_thenBadRquest() {
    // juanpa@gmail.com owns session 1 and it's not empty
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digital/removeDigital/1",
        HttpMethod.DELETE,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void givenUserRemovingOwnSession_whenRemoveDigitalSession_thenOk() {
    // juanpa@gmail.com owns session 6
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digital/removeDigital/6",
        HttpMethod.DELETE,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Boolean.TRUE, response.getBody());
  }

  @Test
  void givenUserRemovingOtherUserSession_whenRemoveDigitalSession_thenForbidden() {
    // juanpa@gmail.com does not own session 2
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digital/removeDigital/2",
        HttpMethod.DELETE,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }
}
