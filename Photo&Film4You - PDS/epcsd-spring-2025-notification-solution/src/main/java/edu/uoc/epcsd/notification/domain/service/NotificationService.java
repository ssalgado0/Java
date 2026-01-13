package edu.uoc.epcsd.notification.domain.service;

import edu.uoc.epcsd.notification.application.kafka.DigitalItemMessage;
import edu.uoc.epcsd.notification.application.kafka.ProductMessage;
import edu.uoc.epcsd.notification.domain.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {

    void notifyProductAvailable(ProductMessage productMessage);

    void notifyDigitalItemPendingReview(DigitalItemMessage digitalItemMessage);

    List<Notification> findAllByUserId(Long userId);

    List<Notification> findUnreadByUserId(Long userId);

    Optional<Notification> findById(Long id);

    Long createNotification(Notification notification);

    void markAsRead(Long id);

    void markAllAsReadByUserId(Long userId);
}
