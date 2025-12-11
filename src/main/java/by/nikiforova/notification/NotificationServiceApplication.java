package by.nikiforova.notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("Запуск notification-service");
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}


