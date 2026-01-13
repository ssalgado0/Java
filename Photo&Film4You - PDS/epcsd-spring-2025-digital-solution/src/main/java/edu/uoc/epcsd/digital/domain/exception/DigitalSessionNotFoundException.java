package edu.uoc.epcsd.digital.domain.exception;

public class DigitalSessionNotFoundException extends NotFoundException {

  public DigitalSessionNotFoundException(Long digitalSessionId) {
    super("The specified DigitalSession id " + digitalSessionId + " does not exist.");
  }

  public DigitalSessionNotFoundException(Long digitalSessionId, String email) {
    super("The specified Id or UserId " + digitalSessionId + " / " + email + " does not exist.");
  }
}
