package edu.uoc.epcsd.productcatalog.application.rest;


import edu.uoc.epcsd.productcatalog.application.rest.request.CreateProductRequest;
import edu.uoc.epcsd.productcatalog.application.rest.response.GetProductResponse;
import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.service.ProductService;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/products")
public class ProductRESTController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        log.trace("getAllProducts");

        return productService.findAllProducts();
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetProductResponse> getProductById(@PathVariable @NotNull Long productId) {
        log.trace("getProductById");

        return productService.findProductById(productId).map(product -> ResponseEntity.ok().body(GetProductResponse.fromDomain(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Product>> findProductsByCriteria(@RequestParam(required = false) String filterTerm,
                                                                Pageable pageable) {

        log.trace("findProductsByCriteria: filterTerm={}", filterTerm);

        if (filterTerm == null || filterTerm.isBlank()) {
            return ResponseEntity.ok(productService.findAllProducts(pageable));
        }

        Page<Product> filteredProducts = productService.findProductsByText(filterTerm.trim(), pageable);
        return ResponseEntity.ok(filteredProducts);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody @NotNull @Valid CreateProductRequest createProductRequest) {
        log.trace("createProduct");

        log.trace("Creating product " + createProductRequest);

        try {
            Long productId = productService.createProduct(Product.builder()
                    .name(createProductRequest.getName())
                    .description(createProductRequest.getDescription())
                    .dailyPrice(createProductRequest.getDailyPrice())
                    .model(createProductRequest.getModel())
                    .brand(createProductRequest.getBrand())
                    .categoryId(createProductRequest.getCategoryId())
                    .build());

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(productId)
                    .toUri();

            return ResponseEntity.created(uri).body(productId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified product category " + createProductRequest.getCategoryId() + " does not exist.", e);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> removeProduct(@PathVariable @NotNull Long productId) {
        log.trace("removeProduct");

        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified product id " + productId + " does not exist.", e);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<GetProductResponse> updateProduct(
        @PathVariable @NotNull Long productId,
        @RequestBody @NotNull @Valid CreateProductRequest updateProductRequest) {

        log.trace("updateProduct productId={}", productId);

        try {
            Product product = Product.builder()
                .id(productId)
                .name(updateProductRequest.getName())
                .description(updateProductRequest.getDescription())
                .dailyPrice(updateProductRequest.getDailyPrice())
                .brand(updateProductRequest.getBrand())
                .model(updateProductRequest.getModel())
                .categoryId(updateProductRequest.getCategoryId())
                .build();

            Product updatedProduct = productService.updateProduct(product);
            return ResponseEntity.ok(GetProductResponse.fromDomain(updatedProduct));

        } catch (EntityNotFoundException e) {
            log.error("The specified product id {} does not exist.", productId, e);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid  data for productId {}. Error: {}", productId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating product with id {}", productId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
