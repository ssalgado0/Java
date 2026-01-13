package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ProductEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "BookingLine")
@ToString(exclude = {"product", "booking"})
@Getter
@Setter
@EqualsAndHashCode(exclude = {"product", "booking"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingLineEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @ManyToOne(optional = false)
  @JoinColumn(name = "booking_id", nullable = false)
  private BookingEntity booking;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "price_per_unit", nullable = false)
  private BigDecimal pricePerUnit;

  @Transient
  public BigDecimal getTotalPrice() {
    return this.pricePerUnit.multiply(BigDecimal.valueOf(this.quantity));
  }
}