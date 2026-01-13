package edu.uoc.epcsd.notification.infrastructure.repository.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.uoc.epcsd.notification.domain.Notification;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Notification")
@Table(name = "notification")
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity implements DomainTranslatable<Notification> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "read", nullable = false)
    private Boolean read;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entity_reference", nullable = false)
    private String entityReference;

    public static NotificationEntity fromDomain(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationEntity.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .read(notification.getRead())
                .type(notification.getType())
                .userId(notification.getUserId())
                .entityReference(notification.getEntity().name())
                .build();
    }

    @Override
    public Notification toDomain() {
        return Notification.builder()
                .id(this.getId())
                .title(this.getTitle())
                .message(this.getMessage())
                .createdAt(this.getCreatedAt())
                .read(this.getRead())
                .type(this.getType())
                .userId(this.getUserId())
                .entity(Notification.EntityReference.valueOf(this.getEntityReference()))
                .build();
    }
}
