package by.nikiforova.notification.controller;

import by.nikiforova.crud.kafka.UserEvent;
import by.nikiforova.notification.dto.CustomEmailRequest;
import by.nikiforova.notification.dto.NotificationRequest;
import by.nikiforova.notification.service.EmailNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailNotificationService emailNotificationService;

    public NotificationController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody NotificationRequest request) {
        emailNotificationService.sendForEvent(new UserEvent(request.getOperation(), request.getEmail()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/send-direct")
    public ResponseEntity<Void> sendDirect(@Valid @RequestBody CustomEmailRequest request) {
        emailNotificationService.sendDirect(request.getEmail(), request.getMessage());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}


