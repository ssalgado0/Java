package edu.uoc.epcsd.productcatalog.domain.service;

import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.exception.ProductHasActiveBookingsException;
import edu.uoc.epcsd.productcatalog.domain.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class ProductServiceImpl implements ProductService {

    private final ItemService itemService;

    private final BookingLineService bookingLineService;

    private final ProductRepository productRepository;

    public List<Product> findAllProducts() {
        return productRepository.findAllProducts();
    }

    @Override
    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAllProducts(pageable);
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findProductById(id);
    }

    public List<Product> findProductsByExample(Product product) {
        return productRepository.findProductsByExample(product);
    }

    public Long createProduct(Product product) {
        return productRepository.createProduct(product);
    }

    public void deleteProduct(Long id) {

        Product product = productRepository.findProductById(id).orElseThrow(IllegalArgumentException::new);

        if (bookingLineService.existsAnyBookingForProduct(id)) {
            throw new ProductHasActiveBookingsException(id);
        }

        itemService.findByProductId(id).stream().forEach(item -> itemService.deleteItem(item.getSerialNumber()));

        productRepository.deleteProduct(product);
    }

    @Override
    public Page<Product> findProductsByText(String filterTerm, Pageable pageable) {
        log.trace("findProductsByText: filterTerm={}", filterTerm);
        return productRepository.findProductsByText(filterTerm, pageable);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.updateProduct(product);
    }
}
