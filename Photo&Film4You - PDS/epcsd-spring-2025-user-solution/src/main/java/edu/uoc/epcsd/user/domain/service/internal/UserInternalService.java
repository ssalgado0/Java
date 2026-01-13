package edu.uoc.epcsd.user.domain.service.internal;

import edu.uoc.epcsd.user.domain.User;
import java.util.Optional;

public interface UserInternalService {

  Optional<User> getUser(String email, String password);
}
