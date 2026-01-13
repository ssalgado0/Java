package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataDigitalSessionRepository extends JpaRepository<DigitalSessionEntity, Long> {

	@Query("select a from digitalsession a where a.email = ?1 ")
	public List<DigitalSessionEntity> findDigitalSessionByUser(String email);
	
	//@Query("select a from DigitalSession a where a.id = ?1 ")
	public Optional<DigitalSessionEntity> getDigitalSessionById(Long id);

	boolean existsByIdAndEmail(Long id, String email);
}
