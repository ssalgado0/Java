package edu.uoc.epcsd.notification.application.rest.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.notification.application.rest.dtos.CreateNotificationRequest;
import edu.uoc.epcsd.notification.domain.service.NotificationService;
import edu.uoc.epcsd.notification.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationInternalRESTController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationInternalRESTControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private NotificationService notificationService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @Test
  void givenValidRequest_whenCreateNotification_thenReturnCreatedWithId() throws Exception {
    // given
    CreateNotificationRequest request = new CreateNotificationRequest(
        100L,
        "Test Notification",
        "This is a test message",
        "info"
    );

    when(notificationService.createNotification(any())).thenReturn(1L);

    // when & then
    mockMvc.perform(post("/internal/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(content().string("1"));
  }

  @Test
  void givenInvalidUserId_whenCreateNotification_thenReturnBadRequest() throws Exception {
    // given
    CreateNotificationRequest request = new CreateNotificationRequest(
        999L,
        "Test Notification",
        "This is a test message",
        "info"
    );

    when(notificationService.createNotification(any()))
        .thenThrow(new IllegalArgumentException("User not found"));

    // when & then
    mockMvc.perform(post("/internal/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "{\"userId\":100,\"message\":\"This is a test message\",\"type\":\"info\"}",
      "{\"userId\":100,\"title\":\"Test Notification\",\"type\":\"info\"}",
      "{\"title\":\"Test Notification\",\"message\":\"This is a test message\",\"type\":\"info\"}"
  })
  void givenRequestWithMissingRequiredFields_whenCreateNotification_thenReturnBadRequest(String jsonRequest) throws Exception {
    // when & then
    mockMvc.perform(post("/internal/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
        .andExpect(status().isBadRequest());
  }
}
