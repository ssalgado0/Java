package edu.uoc.epcsd.digital.domain.repository;

public interface UserRepository {

    boolean findUserByEmail(String email);

}
