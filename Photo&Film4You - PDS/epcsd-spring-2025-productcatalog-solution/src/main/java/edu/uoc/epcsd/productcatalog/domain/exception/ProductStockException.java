package edu.uoc.epcsd.productcatalog.domain.exception;

public class ProductStockException extends BadRequestException {

  private static final long serialVersionUID = 1L;

  public ProductStockException(Long productId, Integer quantity) {
    super("Product with id '" + productId + "' does not have enough stock for quantity '" + quantity
        + "'");
  }
}
