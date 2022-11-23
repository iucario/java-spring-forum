package com.demo.app.notification;

import com.demo.app.auth.AuthService;
import com.demo.app.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthService authService;

    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @GetMapping(produces = "application/json")
    public List<Notification> getNotifications() {
        User user = authService.getCurrentUser();
        return notificationService.getNotifications(user.getName());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteNotification(@PathVariable UUID id) {
        User user = authService.getCurrentUser();
        notificationService.deleteNotification(id, user);
        return ResponseEntity.ok().build();
    }

}
