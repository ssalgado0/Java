package edu.uoc.epcsd.notification.domain.repository;

import edu.uoc.epcsd.notification.domain.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    List<Notification> findAllByUserId(Long userId);

    List<Notification> findUnreadByUserId(Long userId);

    Optional<Notification> findById(Long id);

    Long createNotification(Notification notification);

    void markAsRead(Long id);

    void markAllAsReadByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
