package edu.uoc.epcsd.digital.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext
public abstract class IntegrationTest {

    static final AtomicInteger serverPort = new AtomicInteger(0);
    protected static volatile String userResponseBody = null;
    protected static volatile int userResponseStatus = 200;
    protected static volatile String userResponseContentType = "application/json";
    static HttpServer httpServer;
    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected TestJwtFactory jwtFactory;
    @Autowired
    protected ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.19")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.6.1");

    @LocalServerPort
    protected int port;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) throws Exception {
        if (httpServer == null) {
            httpServer = HttpServer.create(new InetSocketAddress(0), 0);
            httpServer.createContext("/internal", exchange -> {
                byte[] resp = new byte[0];
                if (userResponseBody != null) {
                    resp = userResponseBody.getBytes(StandardCharsets.UTF_8);
                }
                exchange.getResponseHeaders().set("Content-Type", userResponseContentType);
                exchange.sendResponseHeaders(userResponseStatus, resp.length);
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

        registry.add("userService.getUserById.url",
            () -> "http://localhost:" + serverPort.get() + "/internal/users/byEmail/{email}");
    }

    @AfterAll
    static void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @AfterEach
    void cleanUserResponseBody() {
        userResponseBody = null;
    }

    protected String baseUrl() {
        return "http://localhost:" + port;
    }
}
