package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataItemRepository extends JpaRepository<ItemEntity, String> {

    public Optional<ItemEntity> findItemEntityBySerialNumber(String serialNumber);

    public List<ItemEntity> findItemEntitiesByProduct(ProductEntity productEntity);

  @Query("SELECT i.product.id as productId, COUNT(i) as quantity "
      + "FROM Item i "
      + "WHERE i.product.id IN :productIds AND i.status = 'OPERATIONAL' "
      + "GROUP BY i.product.id")
  List<ItemsPerProductCount> countTotalItemsPerProduct(
      @Param("productIds") Collection<Long> productIds);
}
