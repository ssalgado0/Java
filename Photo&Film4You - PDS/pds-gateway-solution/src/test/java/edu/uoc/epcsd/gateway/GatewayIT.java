package edu.uoc.epcsd.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GatewayIT {

  static MockWebServer mockWebServer;

  @LocalServerPort
  int port;

  @Autowired
  TestRestTemplate restTemplate;

  @BeforeAll
  static void setAllUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start(
        18082); // Start on a fixed port, please check application.yaml in test resources
  }

  @AfterAll
  static void shutdown() throws IOException {
    if (mockWebServer != null) {
      mockWebServer.shutdown();
    }
  }

  @Test
  void gatewayRoutesToDownstream() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}")
        .addHeader("Content-Type", "application/json"));

    ResponseEntity<String> response = restTemplate.getForEntity(
        "http://localhost:" + port + "/users/123/profile", String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("{\"ok\":true}");
    assertThat(response.getHeaders()).isNotNull()
        .containsEntry("Content-Type", List.of("application/json"));

    RecordedRequest recorded = mockWebServer.takeRequest();
    assertThat(recorded.getPath()).contains("/users/123/profile");
  }

}
