package by.nikiforova.notification.kafka;

import by.nikiforova.crud.kafka.UserEvent;
import by.nikiforova.notification.service.EmailNotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private static final Logger logger = LogManager.getLogger();
    private final EmailNotificationService emailNotificationService;

    public UserEventListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @KafkaListener(topics = "${kafka.topics.test-topic}", groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleUserEvent(UserEvent event) {
        logger.info("Получено сообщение из Kafka: operation={}, email={}", event.getOperation(), event.getUserEmail());
        emailNotificationService.sendForEvent(event);
    }
}
