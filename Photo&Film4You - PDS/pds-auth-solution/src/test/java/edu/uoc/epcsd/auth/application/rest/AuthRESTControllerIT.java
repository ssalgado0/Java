package edu.uoc.epcsd.auth.application.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import edu.uoc.epcsd.auth.application.rest.request.LoginRequest;
import edu.uoc.epcsd.auth.application.rest.response.LoginResponse;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthRESTControllerIT {

  static final AtomicInteger serverPort = new AtomicInteger(0);
  static HttpServer httpServer;
  static volatile String loginResponseBody = null;
  static volatile int loginResponseStatus = 200;
  static volatile String loginResponseContentType = "application/json";
  @LocalServerPort
  int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @AfterAll
  static void stopServer() {
    if (httpServer != null) {
      httpServer.stop(0);
    }
  }

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) throws Exception {
    if (httpServer == null) {
      httpServer = HttpServer.create(new InetSocketAddress(0), 0);
      httpServer.createContext("/login", exchange -> {
        byte[] resp = new byte[0];
        if (loginResponseBody != null) {
          resp = loginResponseBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        exchange.getResponseHeaders().set("Content-Type", loginResponseContentType);
        exchange.sendResponseHeaders(loginResponseStatus, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
          if (resp.length > 0) {
            os.write(resp);
          }
        }
      });
      httpServer.setExecutor(Executors.newFixedThreadPool(2));
      httpServer.start();
      serverPort.set(httpServer.getAddress().getPort());
    }

    registry.add("userService.login.url", () -> "http://localhost:" + serverPort.get() + "/login");
    registry.add("jwt.secret", () -> "01234567890123456789012345678901");
    registry.add("jwt.expiration", () -> "3600000");
  }

  @Test
  void login_success_returnsToken() throws Exception {
    Map<String, Object> user = Map.of(
        "id", 5,
        "fullName", "Integration User",
        "email", "int@user.com",
        "phoneNumber", "000",
        "role", "USER"
    );

    loginResponseBody = objectMapper.writeValueAsString(user);
    loginResponseStatus = 200;
    loginResponseContentType = "application/json";

    LoginRequest req = new LoginRequest("int@user.com", "pwd");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> entity = new HttpEntity<>(req, headers);

    ResponseEntity<LoginResponse> resp = restTemplate.postForEntity(
        "http://localhost:" + port + "/auth/login", entity, LoginResponse.class);

    assertEquals(200, resp.getStatusCode().value());
    assertNotNull(resp.getBody());
    assertNotNull(resp.getBody().getToken());
  }

  @Test
  void login_userServiceUnavailable_returns401() {
    loginResponseBody = null;
    loginResponseStatus = 500;
    loginResponseContentType = "text/plain";

    LoginRequest req = new LoginRequest("int@user.com", "pwd");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> entity = new HttpEntity<>(req, headers);

    ResponseEntity<String> resp = restTemplate.postForEntity(
        "http://localhost:" + port + "/auth/login", entity, String.class);

    assertEquals(403, resp.getStatusCode().value());
  }
}
