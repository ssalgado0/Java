package edu.uoc.epcsd.digital.domain.exception;

public class DigitalItemNotFoundException extends NotFoundException {

  public DigitalItemNotFoundException(Long digitalItemId) {
    super("The specified DigitalItem id " + digitalItemId + " does not exist.");
  }
}
