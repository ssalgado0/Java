package edu.uoc.epcsd.digital.domain.service;

import edu.uoc.epcsd.digital.domain.DigitalSession;
import edu.uoc.epcsd.digital.domain.DigitalStatus;
import edu.uoc.epcsd.digital.domain.exception.DigitalSessionNotEmptyException;
import edu.uoc.epcsd.digital.domain.exception.DigitalSessionNotFoundException;
import edu.uoc.epcsd.digital.domain.exception.UserNotFoundException;
import edu.uoc.epcsd.digital.domain.repository.DigitalItemRepository;
import edu.uoc.epcsd.digital.domain.repository.DigitalSessionRepository;
import edu.uoc.epcsd.digital.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DigitalSessionServiceImpl implements DigitalSessionService {

    private final DigitalSessionRepository digitalSessionRepository;
    private final DigitalItemRepository digitalItemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DigitalSession> findAllDigitalSession() {
        return digitalSessionRepository.findAllDigitalSession();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DigitalSession> findDigitalSessionByUser(String email) {
        if (!userRepository.findUserByEmail(email)) {
            throw new UserNotFoundException(email);
        }

        return digitalSessionRepository.findDigitalSessionByUser(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DigitalSession> getDigitalSessionById(Long id) {
        return digitalSessionRepository.getDigitalSessionById(id);
    }

    @Override
    @Transactional
    public Long createDigitalSession(DigitalSession digitalSession) {
        return digitalSessionRepository.createDigitalSession(digitalSession);
    }

    @Override
    @Transactional
    public Long updateDigitalSession(Long digitalSessionId, String email, String description) {
        DigitalSession digitalSession = digitalSessionRepository
                .getDigitalSessionById(digitalSessionId)
            .orElseThrow(() -> new DigitalSessionNotFoundException(digitalSessionId, email));

        digitalSession.setEmail(email);
        digitalSession.setDescription(description);
        digitalSession.setStatus(DigitalStatus.NOT_AVAILABLE);

        return digitalSessionRepository.updateDigitalSession(digitalSession);
    }

    @Override
    @Transactional
    public void removeDigitalSession(Long id) {
        DigitalSession digitalSession = digitalSessionRepository
                .getDigitalSessionById(id)
            .orElseThrow(() -> new DigitalSessionNotFoundException(id));

        if (digitalItemRepository.countDigitalItemBySession(id) > 0) {
            log.warn(
                "There are digital items associated with digital session={}. It cannot be deleted",
                id);
            throw new DigitalSessionNotEmptyException(id);
        }

        digitalSessionRepository.removeDigitalSession(digitalSession);
    }
}
