package edu.uoc.epcsd.user.application.rest.internal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.service.UserService;
import edu.uoc.epcsd.user.domain.service.internal.UserInternalService;
import edu.uoc.epcsd.user.infrastructure.security.jwt.JwtTokenProvider;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests unitarios para UserInternalRESTController.
 * Estos tests verifican la lógica del controlador interno sin cargar el contexto completo de Spring Security.
 * Se mockean las dependencias (UserInternalService, UserService) para aislar la lógica del controlador.
 */
@WebMvcTest(UserInternalRESTController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserInternalRESTControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserInternalService userInternalService;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @Test
  void givenValidCredentials_whenLoginUser_thenReturnUser() throws Exception {
    // given
    String email = "test@test.com";
    String password = "password123";
    String requestBody = "{"
        + "\"email\": \"" + email + "\","
        + "\"password\": \"" + password + "\""
        + "}";

    User user = User.builder()
        .id(1L)
        .email(email)
        .fullName("Test User")
        .password("encodedPassword")
        .build();

    when(userInternalService.getUser(email, password)).thenReturn(Optional.of(user));

    // when & then
    mockMvc.perform(post("/internal/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.fullName").value("Test User"));

    verify(userInternalService).getUser(email, password);
  }

  @Test
  void givenInvalidCredentials_whenLoginUser_thenReturnNotFound() throws Exception {
    // given
    String email = "test@test.com";
    String password = "wrongPassword";
    String requestBody = "{"
        + "\"email\": \"" + email + "\","
        + "\"password\": \"" + password + "\""
        + "}";

    when(userInternalService.getUser(email, password)).thenReturn(Optional.empty());

    // when & then
    mockMvc.perform(post("/internal/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isNotFound());

    verify(userInternalService).getUser(email, password);
  }

  @Test
  void givenExistingEmail_whenGetUserByEmail_thenReturnUser() throws Exception {
    // given
    String email = "user@test.com";
    User user = User.builder()
        .id(1L)
        .email(email)
        .fullName("John Doe")
        .password("encodedPassword")
        .build();

    when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));

    // when & then
    mockMvc.perform(get("/internal/users/byEmail/{email}", email)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.fullName").value("John Doe"));

    verify(userService).findUserByEmail(email);
  }

  @Test
  void givenNonExistingEmail_whenGetUserByEmail_thenReturnNotFound() throws Exception {
    // given
    String email = "nonexistent@test.com";
    when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

    // when & then
    mockMvc.perform(get("/internal/users/byEmail/{email}", email)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(userService).findUserByEmail(email);
  }

  @Test
  void givenProductIdAndDate_whenGetUsersToAlert_thenReturnUsers() throws Exception {
    // given
    Long productId = 100L;
    LocalDate availableOnDate = LocalDate.of(2025, 1, 15);

    User user1 = User.builder()
        .id(1L)
        .email("user1@test.com")
        .fullName("User One")
        .password("pass1")
        .build();

    User user2 = User.builder()
        .id(2L)
        .email("user2@test.com")
        .fullName("User Two")
        .password("pass2")
        .build();

    Set<User> users = new HashSet<>();
    users.add(user1);
    users.add(user2);
    when(userService.getUsersToAlert(productId, availableOnDate)).thenReturn(users);

    // when & then
    mockMvc.perform(get("/internal/users/toAlert")
            .param("productId", productId.toString())
            .param("availableOnDate", availableOnDate.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));

    verify(userService).getUsersToAlert(productId, availableOnDate);
  }

  @Test
  void givenNoUsersToAlert_whenGetUsersToAlert_thenReturnEmptyArray() throws Exception {
    // given
    Long productId = 100L;
    LocalDate availableOnDate = LocalDate.of(2025, 1, 15);

    when(userService.getUsersToAlert(productId, availableOnDate))
        .thenReturn(Collections.emptySet());

    // when & then
    mockMvc.perform(get("/internal/users/toAlert")
            .param("productId", productId.toString())
            .param("availableOnDate", availableOnDate.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(userService).getUsersToAlert(productId, availableOnDate);
  }

  @Test
  void givenMultipleUsersToAlert_whenGetUsersToAlert_thenReturnAllUsers() throws Exception {
    // given
    Long productId = 200L;
    LocalDate availableOnDate = LocalDate.of(2025, 2, 1);

    User user1 = User.builder()
        .id(1L)
        .email("alice@test.com")
        .fullName("Alice")
        .password("pass1")
        .build();

    User user2 = User.builder()
        .id(2L)
        .email("bob@test.com")
        .fullName("Bob")
        .password("pass2")
        .build();

    User user3 = User.builder()
        .id(3L)
        .email("charlie@test.com")
        .fullName("Charlie")
        .password("pass3")
        .build();

    Set<User> users = new HashSet<>();
    users.add(user1);
    users.add(user2);
    users.add(user3);
    when(userService.getUsersToAlert(productId, availableOnDate)).thenReturn(users);

    // when & then
    mockMvc.perform(get("/internal/users/toAlert")
            .param("productId", productId.toString())
            .param("availableOnDate", availableOnDate.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));

    verify(userService).getUsersToAlert(productId, availableOnDate);
  }
}
