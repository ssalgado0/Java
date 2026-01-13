package edu.uoc.epcsd.digital.domain.exception;

public class DigitalSessionNotEmptyException extends DomainException {

  public DigitalSessionNotEmptyException(Long digitalSessionId) {
    super(String.format("Digital session with id %d is not empty. Cannot be deleted.",
        digitalSessionId));
  }
}
