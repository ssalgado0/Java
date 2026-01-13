package edu.uoc.epcsd.notification.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataNotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 ORDER BY n.createdAt DESC")
    List<NotificationEntity> findAllByUserId(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 AND n.read = false ORDER BY n.createdAt DESC")
    List<NotificationEntity> findUnreadByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = ?1")
    void markAllAsReadByUserId(Long userId);
}
