package edu.uoc.epcsd.productcatalog.domain.exception;

public class ProductHasActiveBookingsException extends BadRequestException {

  private static final long serialVersionUID = 1L;

  public ProductHasActiveBookingsException(Long productId) {
    super("Product with id " + productId + " has associated reservations and cannot be deleted");
  }
}
