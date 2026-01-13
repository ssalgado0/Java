package edu.uoc.epcsd.auth.domain.repository;

import edu.uoc.epcsd.auth.domain.User;
import java.util.Optional;

public interface UserRepository {

  Optional<User> getLoginUser(String email, String password);
}
