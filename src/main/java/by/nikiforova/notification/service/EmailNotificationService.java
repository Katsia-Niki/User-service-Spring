package by.nikiforova.notification.service;

import by.nikiforova.crud.kafka.UserEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailNotificationService {

    private static final Logger logger = LogManager.getLogger();

    private final JavaMailSender mailSender;
    private final String from;

    public EmailNotificationService(JavaMailSender mailSender,
                                    @Value("${notification.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendForEvent(UserEvent event) {
        logger.info("Начало обработки события: operation={}, email={}", 
                event != null ? event.getOperation() : "null", 
                event != null ? event.getUserEmail() : "null");
        
        if (event == null || !StringUtils.hasText(event.getUserEmail())) {
            logger.warn("Пропущено отправление письма: event пустой или без email");
            return;
        }
        String operation = event.getOperation() == null ? "" : event.getOperation().toUpperCase();
        logger.info("Обработка операции: {}", operation);
        
        String body = switch (operation) {
            case "CREATE" -> "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
            case "DELETE" -> "Здравствуйте! Ваш аккаунт был удалён.";
            default -> null;
        };

        if (body == null) {
            logger.warn("Неизвестная операция {}, письмо не отправлено", event.getOperation());
            return;
        }

        logger.info("Вызов sendEmail для email={}", event.getUserEmail());
        sendEmail(event.getUserEmail(), "Уведомление о пользователе", body);
    }

    public void sendDirect(String email, String message) {
        sendEmail(email, "Уведомление", message);
    }

    private void sendEmail(String to, String subject, String body) {
        logger.info("Попытка отправить письмо: to={}, subject={}, from={}", to, subject, from);
        long startTime = System.currentTimeMillis();
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            logger.info("Вызов mailSender.send() в {}...", Thread.currentThread().getName());
            
            mailSender.send(mailMessage);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Письмо успешно отправлено to={} subject={} за {} мс", to, subject, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Ошибка при отправке письма to={} subject={} после {} мс", to, subject, duration);
            logger.error("Тип исключения: {}", e.getClass().getName());
            logger.error("Сообщение об ошибке: {}", e.getMessage());
            
            Throwable cause = e.getCause();
            if (cause != null) {
                logger.error("Причина (cause): тип={}, сообщение={}", cause.getClass().getName(), cause.getMessage());
                if (cause instanceof jakarta.mail.AuthenticationFailedException) {
                    logger.error("ОШИБКА АУТЕНТИФИКАЦИИ: Неверный пароль или имя пользователя");
                } else if (cause instanceof jakarta.mail.MessagingException) {
                    logger.error("ОШИБКА СООБЩЕНИЯ: {}", cause.getMessage());
                }
            }
            
            logger.error("Полный стек ошибки:", e);
            // Не пробрасываем исключение, чтобы не блокировать Kafka listener
            // В реальном приложении можно добавить механизм повторных попыток
            logger.warn("Письмо НЕ отправлено, но обработка продолжается");
        }
    }
}


