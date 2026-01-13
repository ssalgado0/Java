package edu.uoc.epcsd.productcatalog.domain.exception;

public class ProductNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

	public ProductNotFoundException(Long id) {
        super("Product with id '" + id + "' not found");
    }
}
