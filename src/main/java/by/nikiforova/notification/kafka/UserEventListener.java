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
        try {
            emailNotificationService.sendForEvent(event);
        } catch (Exception e) {
            logger.error("Ошибка при обработке события из Kafka: operation={}, email={}, error={}", 
                    event.getOperation(), event.getUserEmail(), e.getMessage(), e);
            // Не пробрасываем исключение дальше, чтобы Kafka не пытался обработать сообщение повторно
            // В реальном приложении можно добавить механизм повторных попыток или отправки в DLQ
        }
    }
}
