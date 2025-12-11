package by.nikiforova.notification;

import by.nikiforova.crud.kafka.UserEvent;
import by.nikiforova.notification.service.EmailNotificationService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = NotificationServiceApplication.class)
@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=v4ny.nikiforov@yandex.by",
        "notification.mail.from=v4ny.nikiforov@yandex.by"
})
class NotificationServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, null, "smtp"))
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("v4ny.nikiforov@yandex.by", "23nuzome"))
            .withPerMethodLifecycle(true);

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Test
    void shouldSendCreateEmail() throws MessagingException, IOException {
        emailNotificationService.sendForEvent(new UserEvent("CREATE", "v4ny.nikiforov@yandex.by"));

        assertTrue(greenMail.waitForIncomingEmail(2000, 1));
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("v4ny.nikiforov@yandex.by", messages[0].getAllRecipients()[0].toString());
        String content = (String) messages[0].getContent();
        assertTrue(content.contains("успешно создан"));
    }

    @Test
    void shouldSendDeleteEmail() throws MessagingException, IOException {
        emailNotificationService.sendForEvent(new UserEvent("DELETE", "v4ny.nikiforov@yandex.by"));

        assertTrue(greenMail.waitForIncomingEmail(2000, 1));
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("v4ny.nikiforov@yandex.by", messages[0].getAllRecipients()[0].toString());
        String content = (String) messages[0].getContent();
        assertTrue(content.contains("был удалён"));
    }
}