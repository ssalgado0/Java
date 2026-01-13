package edu.uoc.epcsd.notification.application.kafka;

import edu.uoc.epcsd.notification.domain.service.NotificationService;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
@Generated
public class KafkaClassListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaConstants.PRODUCT_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.UNIT_AVAILABLE, groupId = "group-1")
    void productAvailable(ProductMessage productMessage) {
        log.trace("productAvailable");
        notificationService.notifyProductAvailable(productMessage);
    }

    @KafkaListener(topics = KafkaConstants.DIGITAL_ITEM_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.UNIT_AVAILABLE, containerFactory = "kafkaListenerContainerFactoryDI", groupId = "group-1")
    void digitalItemPendingReview (DigitalItemMessage digitalItemMessage) {
        log.trace("digitalItemPendingReview");
        notificationService.notifyDigitalItemPendingReview(digitalItemMessage);

    }
}
