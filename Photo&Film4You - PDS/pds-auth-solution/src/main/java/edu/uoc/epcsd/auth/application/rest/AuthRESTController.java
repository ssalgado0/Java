package edu.uoc.epcsd.auth.application.rest;

import edu.uoc.epcsd.auth.application.rest.request.LoginRequest;
import edu.uoc.epcsd.auth.application.rest.response.LoginResponse;
import edu.uoc.epcsd.auth.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthRESTController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
    log.trace("login");
    return authService.generateToken(loginRequest.getEmail(), loginRequest.getPassword())
        .map(token -> ResponseEntity.ok().body(new LoginResponse(token)))
        .orElse(ResponseEntity.status(403).build());
  }
}
