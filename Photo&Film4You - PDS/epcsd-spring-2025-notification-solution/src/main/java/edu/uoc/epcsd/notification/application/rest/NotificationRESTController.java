package edu.uoc.epcsd.notification.application.rest;

import edu.uoc.epcsd.notification.domain.Notification;
import edu.uoc.epcsd.notification.domain.service.NotificationService;
import edu.uoc.epcsd.notification.infrastructure.security.model.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/notifications")
public class NotificationRESTController {

    private final NotificationService notificationService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getNotifications(@AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("getNotifications");

        List<Notification> userNotifications = notificationService.findAllByUserId(Long.valueOf(currentUser.getId()));
        if ("ADMIN".equals(currentUser.getRole())) {
            List<Notification> allUserNotifications = notificationService.findAllByUserId(0L);
            userNotifications.addAll(allUserNotifications);
        }
        return userNotifications;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/unread")
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getUnreadNotifications(@AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("getUnreadNotifications");

        List<Notification> userNotifications = notificationService.findUnreadByUserId(Long.valueOf(currentUser.getId()));
        if ("ADMIN".equals(currentUser.getRole())) {
            List<Notification> allUserNotifications = notificationService.findAllByUserId(0L);
            userNotifications.addAll(allUserNotifications);
        }

        return userNotifications;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable @NotNull Long notificationId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("markAsRead");

        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("markAllAsRead");

        notificationService.markAllAsReadByUserId(Long.valueOf(currentUser.getId()));
        return ResponseEntity.ok().build();
    }
}
