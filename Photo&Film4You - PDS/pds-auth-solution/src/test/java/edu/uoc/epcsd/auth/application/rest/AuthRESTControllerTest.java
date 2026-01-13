package edu.uoc.epcsd.auth.application.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.auth.application.rest.request.LoginRequest;
import edu.uoc.epcsd.auth.application.rest.response.LoginResponse;
import edu.uoc.epcsd.auth.domain.service.AuthService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthRESTControllerTest {

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthRESTController controller;

  @Test
  void givenAuthServiceReturnsToken_whenLogin_thenReturns200WithBody() {
    when(authService.generateToken("test@test.com", "pwd")).thenReturn(Optional.of("token"));

    ResponseEntity<LoginResponse> resp = controller.login(new LoginRequest("test@test.com", "pwd"));

    assertEquals(200, resp.getStatusCode().value());
    assertNotNull(resp.getBody());
    assertEquals("token", resp.getBody().getToken());
  }

  @Test
  void givenAuthServiceReturnsEmpty_whenLogin_thenReturns403() {
    when(authService.generateToken("test@test.com", "pwd")).thenReturn(Optional.empty());

    ResponseEntity<LoginResponse> resp = controller.login(new LoginRequest("test@test.com", "pwd"));

    assertEquals(403, resp.getStatusCode().value());
    assertNull(resp.getBody());
  }
}
