package edu.uoc.epcsd.user.domain.service.internal;

import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UserInternalServiceImpl implements UserInternalService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Optional<User> getUser(String email, String password) {
    log.info("Getting internal user by email and password");
    return userRepository.findUserByEmail(email)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()));
  }
}
