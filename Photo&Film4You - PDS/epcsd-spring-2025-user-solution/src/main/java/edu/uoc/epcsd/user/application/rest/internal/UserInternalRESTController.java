package edu.uoc.epcsd.user.application.rest.internal;

import edu.uoc.epcsd.user.application.rest.request.LoginRequest;
import edu.uoc.epcsd.user.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.user.domain.service.UserService;
import edu.uoc.epcsd.user.domain.service.internal.UserInternalService;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/internal/users")
public class UserInternalRESTController {

  private final UserInternalService userInternalService;
  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<GetUserResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
    log.trace("loginUser");

    return userInternalService.getUser(loginRequest.getEmail(), loginRequest.getPassword())
        .map(user -> ResponseEntity.ok().body(GetUserResponse.fromDomain(user)))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/byEmail/{email}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GetUserResponse> getUserByEmail(
      @PathVariable @NotNull @Email String email) {
    log.trace("getUserByEmail");

    return userService.findUserByEmail(email)
        .map(user -> ResponseEntity.ok().body(GetUserResponse.fromDomain(user)))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/toAlert")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GetUserResponse[]> getUsersToAlert(@RequestParam @NotNull Long productId, @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableOnDate) {
    log.trace("getUsersToAlert");

    return ResponseEntity.ok().body(userService.getUsersToAlert(productId, availableOnDate).stream().map(GetUserResponse::fromDomain).toArray(GetUserResponse[]::new));
  }
}
