package edu.uoc.epcsd.user.domain.exception;

public class AlertNotFoundException extends NotFoundException {

  public AlertNotFoundException(Long id) {
    super("The specified Alert id " + id + " does not exist.");
  }

  public AlertNotFoundException(Long id, String email) {
    super("The specified Id or AlertId " + id + " / " + email + " does not exist.");
  }
}
