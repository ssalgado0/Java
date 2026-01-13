package edu.uoc.epcsd.notification.application.rest.internal;

import edu.uoc.epcsd.notification.application.rest.dtos.CreateNotificationRequest;
import edu.uoc.epcsd.notification.domain.Notification;
import edu.uoc.epcsd.notification.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/internal/notifications")
public class NotificationInternalRESTController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Long> createNotification(@RequestBody @Valid CreateNotificationRequest createNotificationRequest) {
        log.trace("createNotification");

        try {
            log.info("Creating notification for user " + createNotificationRequest.getUserId() + " - Title: " + createNotificationRequest.getTitle());
            
            Long notificationId = notificationService.createNotification(Notification.builder()
                    .userId(createNotificationRequest.getUserId())
                    .title(createNotificationRequest.getTitle())
                    .message(createNotificationRequest.getMessage())
                    .type(createNotificationRequest.getType())
                    .createdAt(LocalDateTime.now())
                    .read(false)
                    .build());
            
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(notificationId)
                    .toUri();

            return ResponseEntity.created(uri).body(notificationId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified userId " + createNotificationRequest.getUserId() + " does not exist.", e);
        }
    }
}
