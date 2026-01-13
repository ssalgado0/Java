package edu.uoc.epcsd.notification.application.rest;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.notification.domain.Notification;
import edu.uoc.epcsd.notification.domain.service.NotificationService;
import edu.uoc.epcsd.notification.infrastructure.security.jwt.JwtTokenProvider;
import edu.uoc.epcsd.notification.infrastructure.security.model.CurrentUser;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
 * Tests unitarios para NotificationRESTController.
 * Estos tests verifican la lógica del controlador sin cargar el contexto completo de Spring Security.
 * Se mockean las dependencias (NotificationService) para aislar la lógica del controlador.
 * Se mockea el CurrentUser en SecurityContext para simular un usuario autenticado.
 */
@WebMvcTest(NotificationRESTController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationRESTControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private NotificationService notificationService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;
  
  private CurrentUser currentUser;
  
  @BeforeEach
  void setUp() {
    // Configurar un usuario mock para todas las pruebas
    currentUser = new CurrentUser("1", "test@test.com", "Test User", "ROLE_USER");
    
    // Mockear el contexto de seguridad
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(currentUser);
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void givenUserHasNotifications_whenGetNotifications_thenReturnAllUserNotifications()
      throws Exception {
    // given
    Long userId = 1L;
    Notification notification1 = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("Notification 1")
        .message("Message 1")
        .type("info")
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .read(false)
        .build();

    Notification notification2 = Notification.builder()
        .id(2L)
        .userId(userId)
        .title("Notification 2")
        .message("Message 2")
        .type("warning")
        .createdAt(LocalDateTime.of(2025, 1, 2, 15, 30))
        .read(true)
        .build();

    List<Notification> notifications = Arrays.asList(notification1, notification2);
    when(notificationService.findAllByUserId(userId)).thenReturn(notifications);

    // when & then
    mockMvc.perform(get("/notifications")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Notification 1"))
        .andExpect(jsonPath("$[0].read").value(false))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].read").value(true));

    verify(notificationService).findAllByUserId(userId);
  }

  @Test
  void givenUserHasNoNotifications_whenGetNotifications_thenReturnEmptyList() throws Exception {
    // given
    Long userId = 1L;
    when(notificationService.findAllByUserId(userId)).thenReturn(Collections.emptyList());

    // when & then
    mockMvc.perform(get("/notifications")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(notificationService).findAllByUserId(userId);
  }

  @Test
  void givenUserHasUnreadNotifications_whenGetUnreadNotifications_thenReturnOnlyUnread()
      throws Exception {
    // given
    Long userId = 1L;
    Notification unreadNotification1 = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("Unread Notification 1")
        .message("Unread Message 1")
        .type("info")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    Notification unreadNotification2 = Notification.builder()
        .id(3L)
        .userId(userId)
        .title("Unread Notification 2")
        .message("Unread Message 2")
        .type("error")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    List<Notification> unreadNotifications = Arrays.asList(unreadNotification1,
        unreadNotification2);
    when(notificationService.findUnreadByUserId(userId)).thenReturn(unreadNotifications);

    // when & then
    mockMvc.perform(get("/notifications/unread")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].read").value(false))
        .andExpect(jsonPath("$[1].read").value(false));

    verify(notificationService).findUnreadByUserId(userId);
  }

  @Test
  void givenUserHasNoUnreadNotifications_whenGetUnreadNotifications_thenReturnEmptyList()
      throws Exception {
    // given
    Long userId = 1L;
    when(notificationService.findUnreadByUserId(userId)).thenReturn(Collections.emptyList());

    // when & then
    mockMvc.perform(get("/notifications/unread")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(notificationService).findUnreadByUserId(userId);
  }

  @Test
  void givenNotificationId_whenMarkAsRead_thenReturnOk() throws Exception {
    // given
    Long notificationId = 1L;
    doNothing().when(notificationService).markAsRead(notificationId);

    // when & then
    mockMvc.perform(patch("/notifications/{notificationId}/read", notificationId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(notificationService).markAsRead(notificationId);
  }

  @Test
  void givenUserId_whenMarkAllAsRead_thenReturnOk() throws Exception {
    // given
    Long userId = 1L;
    doNothing().when(notificationService).markAllAsReadByUserId(userId);

    // when & then
    mockMvc.perform(patch("/notifications/read-all")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(notificationService).markAllAsReadByUserId(userId);
  }

  @Test
  void givenMultipleNotifications_whenGetNotifications_thenReturnAllInOrder() throws Exception {
    // given
    Long userId = 1L;
    Notification notification1 = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("First")
        .message("First message")
        .type("info")
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .read(true)
        .build();

    Notification notification2 = Notification.builder()
        .id(2L)
        .userId(userId)
        .title("Second")
        .message("Second message")
        .type("warning")
        .createdAt(LocalDateTime.of(2025, 1, 2, 11, 0))
        .read(false)
        .build();

    Notification notification3 = Notification.builder()
        .id(3L)
        .userId(userId)
        .title("Third")
        .message("Third message")
        .type("error")
        .createdAt(LocalDateTime.of(2025, 1, 3, 12, 0))
        .read(false)
        .build();

    List<Notification> notifications = Arrays.asList(notification1, notification2, notification3);
    when(notificationService.findAllByUserId(userId)).thenReturn(notifications);

    // when & then
    mockMvc.perform(get("/notifications")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].title").value("First"))
        .andExpect(jsonPath("$[1].title").value("Second"))
        .andExpect(jsonPath("$[2].title").value("Third"));

    verify(notificationService).findAllByUserId(userId);
  }

  @Test
  void givenMultipleUnreadNotifications_whenGetUnreadNotifications_thenReturnAll()
      throws Exception {
    // given
    Long userId = 1L;
    Notification notification1 = Notification.builder()
        .id(5L)
        .userId(userId)
        .title("Unread 1")
        .message("Message 1")
        .type("success")
        .createdAt(LocalDateTime.now().minusHours(2))
        .read(false)
        .build();

    Notification notification2 = Notification.builder()
        .id(6L)
        .userId(userId)
        .title("Unread 2")
        .message("Message 2")
        .type("info")
        .createdAt(LocalDateTime.now().minusHours(1))
        .read(false)
        .build();

    Notification notification3 = Notification.builder()
        .id(7L)
        .userId(userId)
        .title("Unread 3")
        .message("Message 3")
        .type("warning")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    List<Notification> unreadNotifications = Arrays.asList(notification1, notification2,
        notification3);
    when(notificationService.findUnreadByUserId(userId)).thenReturn(unreadNotifications);

    // when & then
    mockMvc.perform(get("/notifications/unread")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].id").value(5))
        .andExpect(jsonPath("$[1].id").value(6))
        .andExpect(jsonPath("$[2].id").value(7));

    verify(notificationService).findUnreadByUserId(userId);
  }

  @Test
  void givenNotificationsWithDifferentTypes_whenGetNotifications_thenReturnAllTypes()
      throws Exception {
    // given
    Long userId = 1L;
    Notification infoNotification = Notification.builder()
        .id(1L)
        .userId(userId)
        .title("Info")
        .message("Info message")
        .type("info")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    Notification warningNotification = Notification.builder()
        .id(2L)
        .userId(userId)
        .title("Warning")
        .message("Warning message")
        .type("warning")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    Notification errorNotification = Notification.builder()
        .id(3L)
        .userId(userId)
        .title("Error")
        .message("Error message")
        .type("error")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    Notification successNotification = Notification.builder()
        .id(4L)
        .userId(userId)
        .title("Success")
        .message("Success message")
        .type("success")
        .createdAt(LocalDateTime.now())
        .read(false)
        .build();

    List<Notification> notifications = Arrays.asList(infoNotification, warningNotification,
        errorNotification, successNotification);
    when(notificationService.findAllByUserId(userId)).thenReturn(notifications);

    // when & then
    mockMvc.perform(get("/notifications")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(4))
        .andExpect(jsonPath("$[0].type").value("info"))
        .andExpect(jsonPath("$[1].type").value("warning"))
        .andExpect(jsonPath("$[2].type").value("error"))
        .andExpect(jsonPath("$[3].type").value("success"));

    verify(notificationService).findAllByUserId(userId);
  }
}
