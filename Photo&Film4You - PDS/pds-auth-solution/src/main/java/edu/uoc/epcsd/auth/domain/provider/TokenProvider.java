package edu.uoc.epcsd.auth.domain.provider;

import edu.uoc.epcsd.auth.domain.User;

public interface TokenProvider {

  String generateToken(User user);
}
