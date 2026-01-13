package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.uoc.epcsd.productcatalog.domain.Product;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity(name = "Product")
@ToString(exclude = "itemList")
@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends CatalogElement implements DomainTranslatable<Product> {

    @Column(name = "dailyPrice", nullable = false)
    private BigDecimal dailyPrice;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @ManyToOne(optional = false)
    private CategoryEntity category;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<ItemEntity> itemList = Collections.emptyList();

    public static ProductEntity fromDomain(Product product) {
        if (product == null) {
            return null;
        }

        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .dailyPrice(product.getDailyPrice())
                .brand(product.getBrand())
                .model(product.getModel())
                .build();
    }

    @Override
    public Product toDomain() {
        return Product.builder()
                .id(this.getId())
                .name(this.getName())
                .description(this.getDescription())
                .dailyPrice(this.getDailyPrice())
                .brand(this.getBrand())
                .model(this.getModel())
                .categoryId(this.getCategory().getId())
                .build();
    }
}
