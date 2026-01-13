package edu.uoc.epcsd.user.infrastructure.repository.jpa;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAlertRepository extends JpaRepository<AlertEntity, Long> {

    List<AlertEntity> findAllByProductIdAndToGreaterThanEqual(Long productId, LocalDate to);

    @Query("select a from Alert a where a.user.id = ?1 and ((a.from <= ?2 and a.to >= ?2) or (a.from <= ?3 and a.to >= ?3))")
    List<AlertEntity> findAlertsByUserAndInterval(Long productId, LocalDate from, LocalDate to);

    @Query("select a from Alert a where a.user.id = ?1")
    List<AlertEntity> findAlertsByUser(Long productId);

    boolean existsByProductIdAndUserIdAndFromAndTo(Long productId, Long userId, LocalDate from, LocalDate to);

    boolean existsByIdAndUserId(Long id, Long userId);
}
