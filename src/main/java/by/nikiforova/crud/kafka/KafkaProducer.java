package by.nikiforova.crud.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaProducer {

    private static final Logger logger = LogManager.getLogger();

    @Value("${kafka.topics.test-topic}")
    private String topic;

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(UserEvent event) {
        CompletableFuture<SendResult<String, UserEvent>> futureResultSending
                = kafkaTemplate.send(topic, event);

        futureResultSending
                .thenAccept(sendResult ->
                        logger.debug("Message sent successfully operation={} email={} topic={}",
                                event.getOperation(), event.getUserEmail(), sendResult.getRecordMetadata().topic()))
                .exceptionally(exception -> {
                    logger.error("Message not sent! operation={} email={} error={}",
                            event.getOperation(), event.getUserEmail(), exception.getMessage());
                    return null;
                });
    }
}