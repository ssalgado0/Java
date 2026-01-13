package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataDigitalItemRepository extends JpaRepository<DigitalItemEntity, Long> {

	@Query("select a from digitalitem a where a.digitalSession.id = ?1 ")
	public List<DigitalItemEntity> findDigitalItemByDigitalSession(Long digitalSessionId);

	long countByDigitalSessionId(Long digitalSessionId);
	
	//@Query("select a from DigitalItem a where a.id = ?1 ")
	public Optional<DigitalItemEntity> getDigitalItemById(Long id);

	@Query("SELECT (count(a) > 0) FROM digitalitem a WHERE a.id = :id AND a.digitalSession.email = :email")
	boolean existsByIdAndEmail(@Param("id") Long id, @Param("email") String email);
}
