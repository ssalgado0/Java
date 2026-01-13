package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ItemsPerProductCount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataBookingLineRepository extends JpaRepository<BookingLineEntity, Long> {

  @Query("SELECT bl.product.id as productId, SUM(bl.quantity) as quantity "
      + "FROM BookingLine bl "
      + "JOIN bl.booking b "
      + "WHERE bl.product.id IN :productIds "
      + "AND b.status NOT IN ('CANCELLED', 'COMPLETED') "
      + "AND b.startDate < :endDate "
      + "AND b.endDate > :startDate "
      + "GROUP BY bl.product.id "
      + "ORDER BY bl.product.id")
  List<ItemsPerProductCount> countReservedItemsPerProductAndDateRange(
      @Param("productIds") Collection<Long> productIds, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("SELECT CASE WHEN COUNT(bl) > 0 THEN true ELSE false END "
      + "FROM BookingLine bl "
      + "JOIN bl.booking b "
      + "WHERE bl.product.id = :productId ")
  boolean existsAnyBookingForProduct(@Param("productId") Long productId);
}
