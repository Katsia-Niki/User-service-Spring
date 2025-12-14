package by.nikiforova.notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class NotificationServiceApplication {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("Запуск notification-service");
        SpringApplication app = new SpringApplication(NotificationServiceApplication.class);
        app.setDefaultProperties(java.util.Map.of(
                "server.port", "8081",
                "spring.config.name", "application-notification"
        ));
        var context = app.run(args);
        Environment env = context.getEnvironment();
        logger.info("SMTP конфигурация: host={}, port={}, username={}", 
                env.getProperty("spring.mail.host"),
                env.getProperty("spring.mail.port"),
                env.getProperty("spring.mail.username"));
    }
}


