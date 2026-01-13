package edu.uoc.epcsd.productcatalog.domain.exception;

public class CategoryNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

	public CategoryNotFoundException(Long id) {
        super("Category with id '" + id + "' not found");
    }
}
