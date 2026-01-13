package edu.uoc.epcsd.user.application.rest;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.user.domain.Alert;
import edu.uoc.epcsd.user.domain.service.AlertService;
import edu.uoc.epcsd.user.domain.service.SecurityService;
import edu.uoc.epcsd.user.infrastructure.security.model.CurrentUser;
import edu.uoc.epcsd.user.infrastructure.security.jwt.JwtTokenProvider;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para AlertRESTController.
 * Estos tests verifican la lógica del controlador sin cargar el contexto completo de Spring Security.
 * Se mockean las dependencias (AlertService, SecurityService) para aislar la lógica del controlador.
 */
@WebMvcTest(AlertRESTController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlertRESTControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AlertService alertService;

  @MockBean
  private SecurityService securityService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    // Mock SecurityContext to provide CurrentUser for @AuthenticationPrincipal
    CurrentUser currentUser = new CurrentUser("1", "user@example.com", "Test User", "USER");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(currentUser);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void givenAlertsExist_whenGetAllAlerts_thenReturnAllAlerts() throws Exception {
    // given
    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(100L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();

    Alert alert2 = Alert.builder()
        .id(2L)
        .productId(200L)
        .userId(2L)
        .from(LocalDate.of(2025, 2, 1))
        .to(LocalDate.of(2025, 2, 28))
        .build();

    List<Alert> alerts = Arrays.asList(alert1, alert2);
    when(alertService.findAllAlerts()).thenReturn(alerts);

    // when & then
    mockMvc.perform(get("/alerts")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].productId").value(100))
        .andExpect(jsonPath("$[1].id").value(2));

    verify(alertService).findAllAlerts();
  }

  @Test
  void givenNoAlerts_whenGetAllAlerts_thenReturnEmptyList() throws Exception {
    // given
    when(alertService.findAllAlerts()).thenReturn(Collections.emptyList());

    // when & then
    mockMvc.perform(get("/alerts")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(alertService).findAllAlerts();
  }

  @Test
  void givenExistingAlertId_whenGetAlertById_thenReturnAlert() throws Exception {
    // given
    Long alertId = 1L;
    Alert alert = Alert.builder()
        .id(alertId)
        .productId(100L)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();

    when(alertService.findAlertById(alertId)).thenReturn(Optional.of(alert));

    // when & then
    mockMvc.perform(get("/alerts/{alertId}", alertId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(alertId))
        .andExpect(jsonPath("$.productId").value(100))
        .andExpect(jsonPath("$.userId").value(1));

    verify(alertService).findAlertById(alertId);
  }

  @Test
  void givenNonExistingAlertId_whenGetAlertById_thenReturnNotFound() throws Exception {
    // given
    Long alertId = 999L;
    when(alertService.findAlertById(alertId)).thenReturn(Optional.empty());

    // when & then
    mockMvc.perform(get("/alerts/{alertId}", alertId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(alertService).findAlertById(alertId);
  }

  @Test
  void givenAlertId_whenRemoveAlert_thenReturnOk() throws Exception {
    // given
    Long alertId = 1L;
    doNothing().when(alertService).removeAlert(alertId);

    // when & then
    mockMvc.perform(delete("/alerts/{alertId}", alertId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

    verify(alertService).removeAlert(alertId);
  }

  @Test
  void givenProductIdAndDate_whenGetAlertsByProductAndDate_thenReturnMatchingAlerts()
      throws Exception {
    // given
    Long productId = 100L;
    LocalDate date = LocalDate.of(2025, 1, 15);

    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(productId)
        .userId(1L)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();

    List<Alert> alerts = Collections.singletonList(alert1);
    when(alertService.findAlertsByProductAndDate(productId, date)).thenReturn(alerts);

    // when & then
    mockMvc.perform(get("/alerts/byProductAndDate")
            .param("productId", productId.toString())
            .param("availableOnDate", date.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].productId").value(productId));

    verify(alertService).findAlertsByProductAndDate(productId, date);
  }

  @Test
  void givenUserIdAndInterval_whenGetAlertsByUserAndInterval_thenReturnMatchingAlerts()
      throws Exception {
    // given
    Long userId = 1L;
    LocalDate fromDate = LocalDate.of(2025, 1, 1);
    LocalDate toDate = LocalDate.of(2025, 3, 31);

    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(100L)
        .userId(userId)
        .from(LocalDate.of(2025, 1, 15))
        .to(LocalDate.of(2025, 2, 15))
        .build();

    List<Alert> alerts = Collections.singletonList(alert1);
    when(alertService.findAlertsByUserAndInterval(userId, fromDate, toDate))
        .thenReturn(alerts);

    // when & then
    mockMvc.perform(get("/alerts/byUserAndInterval")
            .param("userId", userId.toString())
            .param("fromDate", fromDate.toString())
            .param("toDate", toDate.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].userId").value(userId));

    verify(alertService).findAlertsByUserAndInterval(userId, fromDate, toDate);
  }

  @Test
  void givenMultipleAlertsForProduct_whenGetAlertsByProductAndDate_thenReturnAllMatching()
      throws Exception {
    // given
    Long productId = 100L;
    LocalDate date = LocalDate.of(2025, 6, 15);

    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(productId)
        .userId(1L)
        .from(LocalDate.of(2025, 6, 1))
        .to(LocalDate.of(2025, 6, 30))
        .build();

    Alert alert2 = Alert.builder()
        .id(2L)
        .productId(productId)
        .userId(2L)
        .from(LocalDate.of(2025, 6, 10))
        .to(LocalDate.of(2025, 6, 20))
        .build();

    List<Alert> alerts = Arrays.asList(alert1, alert2);
    when(alertService.findAlertsByProductAndDate(productId, date)).thenReturn(alerts);

    // when & then
    mockMvc.perform(get("/alerts/byProductAndDate")
            .param("productId", productId.toString())
            .param("availableOnDate", date.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].productId").value(productId))
        .andExpect(jsonPath("$[1].productId").value(productId));

    verify(alertService).findAlertsByProductAndDate(productId, date);
  }

  @Test
  void givenNoAlertsInInterval_whenGetAlertsByUserAndInterval_thenReturnEmptyList()
      throws Exception {
    // given
    Long userId = 1L;
    LocalDate fromDate = LocalDate.of(2025, 1, 1);
    LocalDate toDate = LocalDate.of(2025, 3, 31);

    when(alertService.findAlertsByUserAndInterval(userId, fromDate, toDate))
        .thenReturn(Collections.emptyList());

    // when & then
    mockMvc.perform(get("/alerts/byUserAndInterval")
            .param("userId", userId.toString())
            .param("fromDate", fromDate.toString())
            .param("toDate", toDate.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(alertService).findAlertsByUserAndInterval(userId, fromDate, toDate);
  }

  @Test
  void givenCurrentUser_whenGetAlertsByCurrentUser_thenReturnUserAlerts() throws Exception {
    // given
    Long userId = 1L;
    Alert alert1 = Alert.builder()
        .id(1L)
        .productId(100L)
        .userId(userId)
        .from(LocalDate.of(2025, 1, 1))
        .to(LocalDate.of(2025, 1, 31))
        .build();

    List<Alert> alerts = Collections.singletonList(alert1);
    when(alertService.findAlertsByUser(userId)).thenReturn(alerts);

    // when & then
    mockMvc.perform(get("/alerts/byCurrentUser")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].userId").value(userId));

    verify(alertService).findAlertsByUser(userId);
  }

  @Test
  void givenValidCreateAlertRequest_whenCreateAlert_thenReturnCreatedAlert() throws Exception {
    // given
    Long alertId = 1L;
    String requestBody = "{"
        + "\"productId\": 100,"
        + "\"userId\": 1,"
        + "\"from\": \"2025-01-01\","
        + "\"to\": \"2025-01-31\""
        + "}";

    when(alertService.createAlert(any(Alert.class))).thenReturn(alertId);

    // when & then
    mockMvc.perform(post("/alerts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(content().string(alertId.toString()));

    verify(alertService).createAlert(any(Alert.class));
  }

  @Test
  void givenInvalidUserId_whenCreateAlert_thenReturnBadRequest() throws Exception {
    // given
    String requestBody = "{"
        + "\"productId\": 100,"
        + "\"userId\": 999,"
        + "\"from\": \"2025-01-01\","
        + "\"to\": \"2025-01-31\""
        + "}";

    when(alertService.createAlert(any(Alert.class)))
        .thenThrow(new IllegalArgumentException("User not found"));

    // when & then
    mockMvc.perform(post("/alerts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());

    verify(alertService).createAlert(any(Alert.class));
  }
}
