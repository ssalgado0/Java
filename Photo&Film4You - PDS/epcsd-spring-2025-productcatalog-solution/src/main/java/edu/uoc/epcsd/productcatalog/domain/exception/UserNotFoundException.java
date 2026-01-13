package edu.uoc.epcsd.productcatalog.domain.exception;

public class UserNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

	public UserNotFoundException(String email) {
        super("User with email '" + email + "' not found");
    }
}
