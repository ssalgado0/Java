package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SpringDataProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p FROM Product p WHERE "
            + "LOWER(p.name) LIKE LOWER(CONCAT('%', :filterTerm, '%')) OR "
            + "LOWER(p.description) LIKE LOWER(CONCAT('%', :filterTerm, '%')) OR "
            + "LOWER(p.brand) LIKE LOWER(CONCAT('%', :filterTerm, '%')) OR "
            + "LOWER(p.model) LIKE LOWER(CONCAT('%', :filterTerm, '%'))")
    Page<ProductEntity> findByTextSearch(@Param("filterTerm") String filterTerm, Pageable pageable);

}
