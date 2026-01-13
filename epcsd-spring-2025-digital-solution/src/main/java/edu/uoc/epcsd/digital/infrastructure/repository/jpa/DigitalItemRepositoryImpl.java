package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import edu.uoc.epcsd.digital.domain.DigitalItem;
import edu.uoc.epcsd.digital.domain.repository.DigitalItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DigitalItemRepositoryImpl implements DigitalItemRepository {

    private final SpringDataDigitalItemRepository jpaDigitalItemRepository;

    private final SpringDataDigitalSessionRepository jpaDigitalSessionRepository;

    @Override
    public List<DigitalItem> findAllDigitalItem() {
        return jpaDigitalItemRepository.findAll().stream().map(DigitalItemEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<DigitalItem> getDigitalItemById(Long id) {
        return jpaDigitalItemRepository.getDigitalItemById(id).map(DigitalItemEntity::toDomain);
    }

    @Override
    public boolean existsByIdAndEmail(Long id, String email) {
        return email != null && jpaDigitalItemRepository.existsByIdAndEmail(id, email);
    }

    @Override
    public List<DigitalItem> findDigitalItemBySession(Long digitalSessionId) {
        return jpaDigitalItemRepository.findDigitalItemByDigitalSession(digitalSessionId).stream().map(DigitalItemEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public Long countDigitalItemBySession(Long digitalSessionId) {
        return jpaDigitalItemRepository.countByDigitalSessionId(digitalSessionId);
    }

    @Override
    public Long createDigitalItem(DigitalItem digitalItem) {
        DigitalSessionEntity digitalSessionEntity = jpaDigitalSessionRepository.findById(digitalItem.getDigitalsessionid()).orElseThrow(IllegalArgumentException::new);
        DigitalItemEntity digitalItemEntity = DigitalItemEntity.fromDomain(digitalItem);
        digitalItemEntity.setDigitalSession(digitalSessionEntity);
        
        return jpaDigitalItemRepository.save(digitalItemEntity).getId(); 
    }

	@Override
	public Long updateDigitalItem(DigitalItem digitalItem) {
        DigitalItemEntity digitalItemEntity = jpaDigitalItemRepository.findById(digitalItem.getId()).orElseThrow(IllegalArgumentException::new);
        
        DigitalSessionEntity digitalSessionEntity = jpaDigitalSessionRepository.findById(digitalItem.getDigitalsessionid()).orElseThrow(IllegalArgumentException::new);
        digitalItemEntity.setDigitalSession(digitalSessionEntity);
        digitalItemEntity.setDescription(digitalItem.getDescription());
        digitalItemEntity.setLat(digitalItem.getLat());
        digitalItemEntity.setLon(digitalItem.getLon());
        digitalItemEntity.setLink(digitalItem.getLink());
        digitalItemEntity.setStatus(digitalItem.getStatus());
        
        return jpaDigitalItemRepository.save(digitalItemEntity).getId(); 
	}

	@Override
	public void removeDigitalItem(DigitalItem digitalItem) {   
		jpaDigitalItemRepository.delete(DigitalItemEntity.fromDomain(digitalItem));		
	}
	
    
}
