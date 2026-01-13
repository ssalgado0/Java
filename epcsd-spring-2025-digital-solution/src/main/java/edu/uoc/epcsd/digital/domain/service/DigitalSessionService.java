package edu.uoc.epcsd.digital.domain.service;

import edu.uoc.epcsd.digital.domain.DigitalSession;

import java.util.List;
import java.util.Optional;

public interface DigitalSessionService {

    List<DigitalSession> findAllDigitalSession();

    List<DigitalSession> findDigitalSessionByUser(String email);
    
	  Optional<DigitalSession> getDigitalSessionById(Long id);

	  Long createDigitalSession(DigitalSession digitalsession);
    
    Long updateDigitalSession(Long digitalSessionId, String email, String description);
    
    void removeDigitalSession(Long id);

}
