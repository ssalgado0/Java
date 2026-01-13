package edu.uoc.epcsd.notification.domain.service;

import edu.uoc.epcsd.notification.application.kafka.DigitalItemMessage;
import edu.uoc.epcsd.notification.application.kafka.ProductMessage;
import edu.uoc.epcsd.notification.application.rest.dtos.GetDigitalItemResponse;
import edu.uoc.epcsd.notification.application.rest.dtos.GetProductResponse;
import edu.uoc.epcsd.notification.application.rest.dtos.GetUserResponse;
import edu.uoc.epcsd.notification.domain.Notification;
import edu.uoc.epcsd.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Value("${userService.getUsersToAlert.url}")
    private String userServiceUrl;

    @Value("${productService.getProductDetails.url}")
    private String productServiceUrl;

    @Value("${digitalItemService.getDigitalItemToAlert.url}")
    private String digitalItemServiceUrl;

    private final RestTemplate restTemplate;

    @Override
    public void notifyProductAvailable(ProductMessage productMessage) {

        GetProductResponse product = restTemplate.getForEntity(productServiceUrl, GetProductResponse.class, productMessage.getProductId()).getBody();

        GetUserResponse[] usersToAlert = restTemplate.getForEntity(userServiceUrl, GetUserResponse[].class, productMessage.getProductId(), LocalDate.now()).getBody();

        for (GetUserResponse user : usersToAlert) {
            Notification notification = Notification.builder()
                    .userId(user.getId())
                    .title("Producto Disponible")
                    .message("El producto \"" + product.getName() + "\" tiene nuevas unidades disponibles.")
                    .type("info")
                    .createdAt(LocalDateTime.now())
                    .read(false)
                    .entity(Notification.EntityReference.GENERAL)
                    .build();

            createNotification(notification);
            log.info("Notification sent to user " + user.getFullName() + " (" + user.getEmail() + ") for product \"" + product.getName() + "\".");
        }
    }

    @Override
    public void notifyDigitalItemPendingReview(DigitalItemMessage digitalItemMessage) {

        GetDigitalItemResponse digitalItem = restTemplate.getForEntity(digitalItemServiceUrl, GetDigitalItemResponse.class, digitalItemMessage.getDigitalItemId()).getBody();

        if (digitalItem == null) {
            log.info("digital Item not found: ");
            throw new NotFoundException("DigitalItem has been notified but it was not found at database.");
        }

        if(GetDigitalItemResponse.DigitalStatus.REVIEW_PENDING.equals(digitalItem.getStatus())){

            log.info("Digital Item notification pending review received. Sending notification: " + digitalItem);
            Notification notification = Notification.builder()
                    .userId(0L)
                    .title("Contenido Digital pendiente de revisión")
                    .message("Se nos ha informado que el Contenido Digital con ID \"" + digitalItem.getId() + "\" puede contener conflictos de propiedad intelectual. Es URGENTE revisar este contenido.")
                    .type("warning")
                    .createdAt(LocalDateTime.now())
                    .read(false)
                    .entity(Notification.EntityReference.DIGITAL_ITEM)
                    .build();

            createNotification(notification);
            return;
        }

        log.info("Digital Item found but not pending review: " + digitalItem);
    }

    @Override
    public List<Notification> findAllByUserId(Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Long createNotification(Notification notification) {
        return notificationRepository.createNotification(notification);
    }

    @Override
    public void markAsRead(Long id) {
        notificationRepository.markAsRead(id);
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
