package edu.uoc.epcsd.auth.domain.service;

import edu.uoc.epcsd.auth.domain.provider.TokenProvider;
import edu.uoc.epcsd.auth.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final TokenProvider jwtTokenProvider;

  @Override
  public Optional<String> generateToken(String email, String password) {
    log.info("Generating token for user with email: {}", email);
    return userRepository.getLoginUser(email, password)
        .map(jwtTokenProvider::generateToken);
  }
}
