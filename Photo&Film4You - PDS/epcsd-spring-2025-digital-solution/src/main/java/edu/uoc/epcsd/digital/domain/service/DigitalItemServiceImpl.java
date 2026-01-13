package edu.uoc.epcsd.digital.domain.service;

import edu.uoc.epcsd.digital.domain.DigitalItem;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import edu.uoc.epcsd.digital.domain.exception.DigitalSessionNotFoundException;
import edu.uoc.epcsd.digital.domain.repository.DigitalItemRepository;
import java.util.List;
import java.util.Optional;

import edu.uoc.epcsd.digital.infrastructure.kafka.DigitalItemMessage;
import edu.uoc.epcsd.digital.infrastructure.kafka.KafkaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DigitalItemServiceImpl implements DigitalItemService {

	private final DigitalItemRepository digitalItemRepository;

	private final KafkaTemplate<String, DigitalItemMessage> digitalItemKafkaTemplate;

	@Value("${userService.getUserById.url}")
	private String getUserById;

	@Override
	@Transactional(readOnly = true)
	public List<DigitalItem> findAllDigitalItem() {
		return digitalItemRepository.findAllDigitalItem();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DigitalItem> findDigitalItemBySession(Long digitalSessionId) {    		
    	return digitalItemRepository.findDigitalItemBySession(digitalSessionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Long countDigitalItemBySession(Long digitalSessionId) {
		return digitalItemRepository.countDigitalItemBySession(digitalSessionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<DigitalItem> getDigitalItemById(Long id) {
        return digitalItemRepository.getDigitalItemById(id);
	}

	@Override
	@Transactional
	public Long addDigitalItem(DigitalItem digitalItem) {
		return digitalItemRepository.createDigitalItem(digitalItem);
	}

	@Override
	@Transactional
	public Long updateDigitalItem(Long digitalItemId, String description, String link, Double lat,
			Double lon) {

		DigitalItem digitalItem = digitalItemRepository.getDigitalItemById(digitalItemId)
				.orElseThrow(() -> new DigitalSessionNotFoundException(digitalItemId));

		digitalItem.setDescription(description);
		digitalItem.setLink(link);
		digitalItem.setLon(lon);
		digitalItem.setLat(lat);

		return digitalItemRepository.updateDigitalItem(digitalItem);
	}

	@Override
	@Transactional
	public void dropDigitalItem(Long id) {
		DigitalItem digitalItem = digitalItemRepository.getDigitalItemById(id)
				.orElseThrow(() -> new DigitalSessionNotFoundException(id));

		digitalItemRepository.removeDigitalItem(digitalItem);
	}

	@Override
	@Transactional
	public void setDigitalItemForReview(Long digitalItemId) {
		DigitalItem digitalItem = digitalItemRepository.getDigitalItemById(digitalItemId)
				.orElseThrow(() -> new DigitalSessionNotFoundException(digitalItemId));

		digitalItem.setStatus(DigitalStatus.REVIEW_PENDING);

		digitalItemRepository.updateDigitalItem(digitalItem);

		digitalItemKafkaTemplate.send(KafkaConstants.DIGITAL_ITEM_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.UNIT_AVAILABLE, DigitalItemMessage.builder().digitalItemId(digitalItemId).build());
	}

	@Override
	@Transactional
	public void approvePendingDigitalItem(Long digitalItemId) {
		DigitalItem digitalItem = digitalItemRepository.getDigitalItemById(digitalItemId)
				.orElseThrow(() -> new DigitalSessionNotFoundException(digitalItemId));

		if (!digitalItem.getStatus().equals(DigitalStatus.REVIEW_PENDING)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The specified digital item with ID " + digitalItemId + " is not under review status.");
		}
		digitalItem.setStatus(DigitalStatus.AVAILABLE);

		digitalItemRepository.updateDigitalItem(digitalItem);
	}

	@Override
	@Transactional
	public void rejectPendingDigitalItem(Long digitalItemId) {
		DigitalItem digitalItem = digitalItemRepository.getDigitalItemById(digitalItemId)
				.orElseThrow(() -> new DigitalSessionNotFoundException(digitalItemId));

		if (!digitalItem.getStatus().equals(DigitalStatus.REVIEW_PENDING)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The specified digital item with ID " + digitalItemId + " is not under review status.");
		}
		digitalItem.setStatus(DigitalStatus.NOT_AVAILABLE);

		digitalItemRepository.updateDigitalItem(digitalItem);
		
	}
	
}
