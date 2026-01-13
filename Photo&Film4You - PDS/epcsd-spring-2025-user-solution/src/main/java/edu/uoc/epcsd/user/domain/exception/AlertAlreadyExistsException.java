package edu.uoc.epcsd.user.domain.exception;

public class AlertAlreadyExistsException extends DomainException {
    private static final long serialVersionUID = 1L;

    public AlertAlreadyExistsException(Long productId, Long userId, String from, String to) {
        super("Alert for product " + productId + " and user " + userId + " between " + from + " and " + to + " already exists");
    }
}
