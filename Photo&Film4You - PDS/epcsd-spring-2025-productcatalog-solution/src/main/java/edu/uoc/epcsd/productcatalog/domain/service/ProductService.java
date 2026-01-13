package edu.uoc.epcsd.productcatalog.domain.service;

import edu.uoc.epcsd.productcatalog.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    List<Product> findAllProducts();

    Page<Product> findAllProducts(Pageable pageable);

    Optional<Product> findProductById(Long id);

    List<Product> findProductsByExample(Product build);

    Long createProduct(Product product);

    void deleteProduct(Long id);

    Page<Product> findProductsByText(String filterTerm, Pageable pageable);

    Product updateProduct(Product product);
}
