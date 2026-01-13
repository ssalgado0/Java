package edu.uoc.epcsd.notification.infrastructure.repository.jpa;

import edu.uoc.epcsd.notification.domain.Notification;
import edu.uoc.epcsd.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationRepositoryImpl implements NotificationRepository {

    private final SpringDataNotificationRepository jpaRepository;

    @Override
    public List<Notification> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(NotificationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        return jpaRepository.findUnreadByUserId(userId).stream()
                .map(NotificationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpaRepository.findById(id).map(NotificationEntity::toDomain);
    }

    @Override
    public Long createNotification(Notification notification) {
        NotificationEntity notificationEntity = NotificationEntity.fromDomain(notification);
        return jpaRepository.save(notificationEntity).getId();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        jpaRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            jpaRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void markAllAsReadByUserId(Long userId) {
        jpaRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return jpaRepository.existsByIdAndUserId(id, userId);
    }
}
