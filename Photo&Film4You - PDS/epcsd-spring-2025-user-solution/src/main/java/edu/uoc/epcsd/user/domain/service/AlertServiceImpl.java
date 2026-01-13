package edu.uoc.epcsd.user.domain.service;

import edu.uoc.epcsd.user.domain.exception.AlertAlreadyExistsException;
import edu.uoc.epcsd.user.domain.exception.AlertNotFoundException;
import edu.uoc.epcsd.user.domain.exception.ProductNotFoundException;
import edu.uoc.epcsd.user.domain.repository.ProductRepository;
import edu.uoc.epcsd.user.domain.Alert;
import edu.uoc.epcsd.user.domain.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    private final ProductRepository productRepository;


    public List<Alert> findAllAlerts() {
        return alertRepository.findAllAlerts();
    }

    public Optional<Alert> findAlertById(Long id) {
        return alertRepository.findAlertById(id);
    }

    @Override
    public List<Alert> findAlertsByProductAndDate(Long productId, LocalDate availableOnDate) {
        return alertRepository.findAlertsByProductAndDate(productId, availableOnDate);
    }

    @Override
    public List<Alert> findAlertsByUserAndInterval(Long userId, LocalDate fromDate, LocalDate toDate) {
        return alertRepository.findAlertsByUserAndInterval(userId, fromDate, toDate);
    }

    @Override
    public List<Alert> findAlertsByUser(Long userId) {
        return alertRepository.findAlertsByUser(userId);
    }

    public Long createAlert(Alert alert) throws ProductNotFoundException {
        if (!productRepository.existsById(alert.getProductId())) {
            throw new ProductNotFoundException(alert.getProductId());
        }

        if (alertRepository.existsByProductIdAndUserIdAndFromAndTo(alert.getProductId(), alert.getUserId(), alert.getFrom(), alert.getTo())) {
            throw new AlertAlreadyExistsException(alert.getProductId(), alert.getUserId(), alert.getFrom().toString(), alert.getTo().toString());
        }
        return alertRepository.createAlert(alert);
    }

    @Override
    public void removeAlert(Long id) {
        alertRepository.removeAlert(id);
    }
}
