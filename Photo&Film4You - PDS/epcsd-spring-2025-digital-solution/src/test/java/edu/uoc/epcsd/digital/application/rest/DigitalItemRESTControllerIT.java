package edu.uoc.epcsd.digital.application.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import edu.uoc.epcsd.digital.application.rest.request.CreateDigitalItemRequest;
import edu.uoc.epcsd.digital.domain.DigitalItem;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

class DigitalItemRESTControllerIT extends IntegrationTest {

  private CurrentUser currentUser;

  static Stream<Arguments> anonymousRequests() {
    return Stream.of(
        arguments("/digitalItem/allItems", HttpMethod.GET, new HttpEntity<>(null)),
        arguments("/digitalItem/1", HttpMethod.GET, new HttpEntity<>(null)),
        arguments("/digitalItem/digitalItemBySession?digitalSessionId=1", HttpMethod.GET,
            new HttpEntity<>(null)),
        arguments("/digitalItem/addItem", HttpMethod.POST,
            new HttpEntity<>(new CreateDigitalItemRequest(1L, "test", 1D, 1D, "test"))),
        arguments("/digitalItem/updateItem/1", HttpMethod.PUT,
            new HttpEntity<>(new CreateDigitalItemRequest(1L, "test", 1D, 1D, "test"))),
        arguments("/digitalItem/reviewDigitalItem/1", HttpMethod.PATCH, new HttpEntity<>(null)),
        arguments("/digitalItem/approveDigitalItem/1", HttpMethod.PATCH, new HttpEntity<>(null)),
        arguments("/digitalItem/rejectDigitalItem/1", HttpMethod.PATCH, new HttpEntity<>(null)),
        arguments("/digitalItem/dropItem/1", HttpMethod.DELETE, new HttpEntity<>(null)));
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
  void givenAdminUser_whenGetAllDigitalItem_thenOk() {
    CurrentUser adminUser = CurrentUser.builder()
        .id("2")
        .email("admin@test.com")
        .role("ADMIN")
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(adminUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<List<DigitalItem>> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/allItems",
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        });

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody()).isNotNull().isNotEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "/digitalItem/allItems",
      "/digitalItem/3",
      "/digitalItem/digitalItemBySession?digitalSessionId=3"
  })
  void givenUser_whenAccessToForbiddenEndpoint_thenForbidden(String endpoint) {
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
  @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
  void givenUserWithAccess_whenGetDigitalItemById_thenOk() {
    // juanpa@gmail.com has access to item 1 (session 1)
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<DigitalItem> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/1",
        HttpMethod.GET,
        entity,
        DigitalItem.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
  }

  @Test
  @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
  void givenAdminUser_whenGetDigitalItemById_thenOk() {
    CurrentUser adminUser = CurrentUser.builder()
        .id("2")
        .email("admin@test.com")
        .role("ADMIN")
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(adminUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<DigitalItem> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/1",
        HttpMethod.GET,
        entity,
        DigitalItem.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
  }

  @Test
  void givenUserWithAccess_whenFindDigitalItemBySession_thenOk() {
    // juanpa@gmail.com has access to session 1
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<List<DigitalItem>> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/digitalItemBySession?digitalSessionId=1",
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        });

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody())
        .isNotNull()
        .hasSize(2)
        .extracting(DigitalItem::getDigitalsessionid)
        .contains(1L);
  }

  @Test
  void givenUserWithAccess_whenCountDigitalItemBySession_thenOk() {
    // juanpa@gmail.com has access to session 1
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Long> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/digitalItemBySession?digitalSessionId=1&count=true",
        HttpMethod.GET,
        entity,
        Long.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody())
        .isNotNull()
        .isEqualTo(2L);
  }

  @Test
  void givenUserAddingItemToOwnSession_whenAddDigitalItem_thenCreated() {
    // juanpa@gmail.com owns session 1
    CreateDigitalItemRequest request = new CreateDigitalItemRequest(1L, "New Item", 10.0, 10.0,
        "http://link.com");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalItemRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Long> response = restTemplate.postForEntity(
        baseUrl() + "/digitalItem/addItem",
        entity,
        Long.class);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void givenUserAddingItemToOtherSession_whenAddDigitalItem_thenForbidden() {
    // juanpa@gmail.com does not own session 3
    CreateDigitalItemRequest request = new CreateDigitalItemRequest(3L, "New Item", 10.0, 10.0,
        "http://link.com");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalItemRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Object> response = restTemplate.postForEntity(
        baseUrl() + "/digitalItem/addItem",
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenUserUpdatingOwnItem_whenUpdateDigitalItem_thenOk() {
    // juanpa@gmail.com owns item 1 (session 1)
    CreateDigitalItemRequest request = new CreateDigitalItemRequest(1L, "Updated Item", 10.0, 10.0,
        "http://link.com");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalItemRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/updateItem/1",
        HttpMethod.PUT,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Boolean.TRUE, response.getBody());
  }

  @Test
  void givenUserUpdatingOtherItem_whenUpdateDigitalItem_thenForbidden() {
    // juanpa@gmail.com does not own item 3 (session 3)
    CreateDigitalItemRequest request = new CreateDigitalItemRequest(3L, "Updated Item", 10.0, 10.0,
        "http://link.com");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<CreateDigitalItemRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/updateItem/3",
        HttpMethod.PUT,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
  void givenAdminUser_whenSetDigitalItemForReview_thenOk() {
    CurrentUser adminUser = CurrentUser.builder()
        .id("2")
        .email("admin@test.com")
        .role("ADMIN")
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(adminUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/reviewDigitalItem/1",
        HttpMethod.PATCH,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Boolean.TRUE, response.getBody());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "/digitalItem/reviewDigitalItem/1",
      "/digitalItem/approveDigitalItem/1",
      "/digitalItem/rejectDigitalItem/1"
  })
  void givenUser_whenForbiddenPatchEndpoint_thenForbidden(String endpoint) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + endpoint,
        HttpMethod.PATCH,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void givenUserDroppingOwnItem_whenDropDigitalItem_thenOk() {
    // juanpa@gmail.com owns item 1 (session 1)
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/dropItem/1",
        HttpMethod.DELETE,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Boolean.TRUE, response.getBody());
  }

  @Test
  void givenUserDroppingOtherItem_whenDropDigitalItem_thenForbidden() {
    // juanpa@gmail.com does not own item 3 (session 3)
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtFactory.generateToken(currentUser));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Object> response = restTemplate.exchange(
        baseUrl() + "/digitalItem/dropItem/3",
        HttpMethod.DELETE,
        entity,
        Object.class);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }
}