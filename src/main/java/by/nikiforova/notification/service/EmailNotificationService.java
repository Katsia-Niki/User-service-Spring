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
        if (event == null || !StringUtils.hasText(event.getUserEmail())) {
            logger.warn("Пропущено отправление письма: event пустой или без email");
            return;
        }
        String operation = event.getOperation() == null ? "" : event.getOperation().toUpperCase();
        String body = switch (operation) {
            case "CREATE" -> "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
            case "DELETE" -> "Здравствуйте! Ваш аккаунт был удалён.";
            default -> null;
        };

        if (body == null) {
            logger.warn("Неизвестная операция {}, письмо не отправлено", event.getOperation());
            return;
        }

        sendEmail(event.getUserEmail(), "Уведомление о пользователе", body);
    }

    public void sendDirect(String email, String message) {
        sendEmail(email, "Уведомление", message);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailSender.send(mailMessage);
        logger.info("Письмо отправлено to={} subject={}", to, subject);
    }
}


