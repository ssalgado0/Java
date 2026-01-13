package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductRepositoryImpl implements ProductRepository {

    private final SpringDataProductRepository jpaRepository;

    private final SpringDataCategoryRepository jpaCategoryRepository;

    @Override
    public List<Product> findAllProducts() {
        return jpaRepository.findAll().stream().map(ProductEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Product> findAllProducts(Pageable pageable) {
        Page<ProductEntity> allProducts = jpaRepository.findAll(pageable);
        return allProducts.map(ProductEntity::toDomain);
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return jpaRepository.findById(id).map(ProductEntity::toDomain);
    }

    @Override
    public List<Product> findProductsByExample(Product product) {

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", contains().ignoreCase());


        ProductEntity productEntity = ProductEntity.fromDomain(product);
        if (product.getCategoryId() != null) {
            productEntity.setCategory(jpaCategoryRepository.findById((product.getCategoryId())).orElseThrow(IllegalArgumentException::new));
        }

        Example<ProductEntity> example = Example.of(productEntity, matcher);

        return jpaRepository.findAll(example).stream().map(ProductEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public Long createProduct(Product product) {
        ProductEntity productEntity = ProductEntity.fromDomain(product);
        productEntity.setCategory(jpaCategoryRepository.getById(product.getCategoryId()));

        return jpaRepository.save(productEntity).getId();
    }

    @Override
    public void deleteProduct(Product product) {
        jpaRepository.delete(ProductEntity.fromDomain(product));
    }

    @Override
    public Page<Product> findProductsByText(String filterTerm, Pageable pageable) {
        Page<ProductEntity> entityPage = jpaRepository.findByTextSearch(filterTerm, pageable);

        List<Product> domainProducts = entityPage.getContent().stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(domainProducts, pageable, entityPage.getTotalElements());
    }

    @Override
    public Product updateProduct(Product product) {
        ProductEntity productEntity = jpaRepository.findById(product.getId())
            .orElseThrow(IllegalArgumentException::new);
        productEntity.setName(product.getName());
        productEntity.setDescription(product.getDescription());
        productEntity.setDailyPrice(product.getDailyPrice());
        productEntity.setBrand(product.getBrand());
        productEntity.setModel(product.getModel());
        productEntity.setCategory(jpaCategoryRepository.findById((product.getCategoryId()))
            .orElseThrow(IllegalArgumentException::new));

        return jpaRepository.save(productEntity).toDomain();
    }
}
