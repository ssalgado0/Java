package edu.uoc.epcsd.auth.domain.service;

import java.util.Optional;

public interface AuthService {

  Optional<String> generateToken(String email, String password);
}
