package edu.uoc.epcsd.productcatalog.domain.repository;

import edu.uoc.epcsd.productcatalog.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    List<Product> findAllProducts();

    Page<Product> findAllProducts(Pageable pageable);

    Optional<Product> findProductById(Long id);

    List<Product> findProductsByExample(Product product);

    Long createProduct(Product product);

    void deleteProduct(Product product);

    Page<Product> findProductsByText(String filterTerm, Pageable pageable);

    Product updateProduct(Product product);
}
