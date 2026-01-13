package edu.uoc.epcsd.digital.domain.repository;

import edu.uoc.epcsd.digital.domain.DigitalSession;
import java.util.List;
import java.util.Optional;

public interface DigitalSessionRepository {

    List<DigitalSession> findDigitalSessionByUser(String email);

    Long createDigitalSession(DigitalSession digitalSession);
    
    Long updateDigitalSession(DigitalSession digitalSession);
	
    void removeDigitalSession(DigitalSession digitalSession);

	List<DigitalSession> findAllDigitalSession();

	Optional<DigitalSession> getDigitalSessionById(Long id);

  boolean existsByIdAndEmail(Long id, String email);
	
}
