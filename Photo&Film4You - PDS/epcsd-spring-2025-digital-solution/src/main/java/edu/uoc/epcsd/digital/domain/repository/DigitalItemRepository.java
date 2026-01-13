package edu.uoc.epcsd.digital.domain.repository;

import edu.uoc.epcsd.digital.domain.DigitalItem;
import java.util.List;
import java.util.Optional;

public interface DigitalItemRepository {

    List<DigitalItem> findDigitalItemBySession(Long digitalSessionId);

  Long countDigitalItemBySession(Long digitalSessionId);

    Long createDigitalItem(DigitalItem digitalItem);
    
    Long updateDigitalItem(DigitalItem digitalItem);
	
    void removeDigitalItem(DigitalItem digitalItem);

	List<DigitalItem> findAllDigitalItem();

	Optional<DigitalItem> getDigitalItemById(Long id);

  boolean existsByIdAndEmail(Long id, String email);
}
