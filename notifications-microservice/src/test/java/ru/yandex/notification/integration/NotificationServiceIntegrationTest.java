package ru.yandex.notification.integration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.notification.KafkaTestConfig;
import ru.yandex.notification.model.Message;
import ru.yandex.notification.service.NotificationsService;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "notification.test")
@Import(KafkaTestConfig.class)
@ActiveProfiles("test")
public class NotificationServiceIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    private NotificationsService notificationService;

    @Test
    void testKafkaListener() {
        String email = "test@example.com";
        String text = "Hello Kafka!";
        Message msg = new Message(email, text);

        kafkaTemplate.send("notification.test", msg);
        kafkaTemplate.flush();
        var messages = notificationService.getOldMessagesByEmail(email).size();
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    assertEquals(1, messages);
                });
    }
}
