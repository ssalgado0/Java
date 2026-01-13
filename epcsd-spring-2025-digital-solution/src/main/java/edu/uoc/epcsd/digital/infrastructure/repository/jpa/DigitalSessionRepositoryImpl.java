package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import edu.uoc.epcsd.digital.domain.DigitalSession;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import edu.uoc.epcsd.digital.domain.exception.UserNotFoundException;
import edu.uoc.epcsd.digital.domain.repository.DigitalSessionRepository;
import edu.uoc.epcsd.digital.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DigitalSessionRepositoryImpl implements DigitalSessionRepository {

    private final SpringDataDigitalSessionRepository jpaDigitalSessionRepository;
    
    private final UserRepository userRepository;
    
    @Override
    public List<DigitalSession> findAllDigitalSession() {
        return jpaDigitalSessionRepository.findAll().stream().map(DigitalSessionEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<DigitalSession> getDigitalSessionById(Long id) {
        return jpaDigitalSessionRepository.getDigitalSessionById(id).map(DigitalSessionEntity::toDomain);
    }

  @Override
  public boolean existsByIdAndEmail(Long id, String email) {
    return email != null && jpaDigitalSessionRepository.existsByIdAndEmail(id, email);
  }

  @Override
    public List<DigitalSession> findDigitalSessionByUser(String email) {
        return jpaDigitalSessionRepository.findDigitalSessionByUser(email).stream().map(DigitalSessionEntity::toDomain).collect(Collectors.toList());
    }   

    @Override
    public Long createDigitalSession(DigitalSession digitalSession) {
		if (!userRepository.findUserByEmail(digitalSession.getEmail())) {
      throw new UserNotFoundException(digitalSession.getEmail());
    }
      DigitalSessionEntity digitalsessionEntity = DigitalSessionEntity.fromDomain(digitalSession);
      digitalsessionEntity.setEmail(digitalSession.getEmail());

      return jpaDigitalSessionRepository.save(digitalsessionEntity).getId();
    }

	@Override
	public Long updateDigitalSession(DigitalSession digitalSession) {
        
		if (!userRepository.findUserByEmail(digitalSession.getEmail())) {
      throw new UserNotFoundException(digitalSession.getEmail());
    }
    DigitalSessionEntity digitalSessionEntity = jpaDigitalSessionRepository.findById(
        digitalSession.getId()).orElseThrow(IllegalArgumentException::new);

    digitalSessionEntity.setDescription(digitalSession.getDescription());
    digitalSessionEntity.setEmail(digitalSession.getEmail());
    digitalSessionEntity.setStatus(DigitalStatus.AVAILABLE);

    return jpaDigitalSessionRepository.save(digitalSessionEntity).getId();
	}

	@Override
	public void removeDigitalSession(DigitalSession digitalSession) {   
		jpaDigitalSessionRepository.delete(DigitalSessionEntity.fromDomain(digitalSession));		
	}
	
    
}
