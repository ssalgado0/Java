package edu.uoc.epcsd.productcatalog.infrastructure.security.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import edu.uoc.epcsd.productcatalog.domain.exception.BadRequestException;
import edu.uoc.epcsd.productcatalog.domain.exception.NotFoundException;
import edu.uoc.epcsd.productcatalog.domain.exception.ProductHasActiveBookingsException;
import edu.uoc.epcsd.productcatalog.domain.exception.ProductStockException;
import edu.uoc.epcsd.productcatalog.infrastructure.security.jwt.JwtValidationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    log.info("Malformed JSON request: ", e);
    ErrorResponse errorResponse = new ErrorResponse("Malformed JSON request");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {
    List<String> validations = e.getBindingResult().getAllErrors().stream()
        .map(ObjectError::getDefaultMessage).collect(
            Collectors.toList());
    ErrorResponse errorResponse = new ErrorResponse("Validation error", validations);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
  public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(JwtValidationException.class)
  public ResponseEntity<ErrorResponse> handleJwtException(JwtValidationException ex) {
    log.warn("JWT validation error: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse("Access denied"));
  }

  @ExceptionHandler(ProductStockException.class)
  public ResponseEntity<ErrorResponse> handleProductStockException(ProductStockException ex) {
    log.info("Product stock error: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(ProductHasActiveBookingsException.class)
  public ResponseEntity<ErrorResponse> handleProductHasActiveBookingsException(
      ProductHasActiveBookingsException ex) {
    log.info("Product has active bookings: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
    log.warn("Response status exception: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(ex.getStatus())
        .body(new ErrorResponse(ex.getReason()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("Internal server error"));
  }

  @Data
  @AllArgsConstructor
  public static class ErrorResponse {

    private String message;

    @JsonInclude(Include.NON_EMPTY)
    private List<String> validations;

    public ErrorResponse(String message) {
      this.message = message;
    }
  }
}