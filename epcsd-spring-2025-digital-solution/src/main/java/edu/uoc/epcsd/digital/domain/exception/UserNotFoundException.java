package edu.uoc.epcsd.digital.domain.exception;

public class UserNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

	public UserNotFoundException(String email) {
        super("User with id '" + email + "' not found");
    }
}
