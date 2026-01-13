package edu.uoc.epcsd.auth.infrastructure.repository.rest;

import edu.uoc.epcsd.auth.application.rest.request.LoginRequest;
import edu.uoc.epcsd.auth.domain.User;
import edu.uoc.epcsd.auth.domain.mapper.UserMapper;
import edu.uoc.epcsd.auth.domain.repository.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

  private final RestTemplate restTemplate;
  private final UserMapper userMapper;
  private final String userServiceLoginUrl;

  public UserRepositoryImpl(RestTemplate restTemplate, UserMapper userMapper,
      @Value("${userService.login.url}") String userServiceLoginUrl) {
    this.restTemplate = restTemplate;
    this.userMapper = userMapper;
    this.userServiceLoginUrl = userServiceLoginUrl;
  }

  @Override
  public Optional<User> getLoginUser(String email, String password) {
    log.info("Getting user by email and password via REST call");
    LoginRequest request = new LoginRequest(email, password);
    ResponseEntity<GetUserResponse> response = restTemplate.
        postForEntity(userServiceLoginUrl, request, GetUserResponse.class);
    log.info("Received response with status code: {}", response.getStatusCode());

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      GetUserResponse userResponse = response.getBody();
      return Optional.of(userMapper.toDomain(userResponse));
    }

    return Optional.empty();
  }
}
