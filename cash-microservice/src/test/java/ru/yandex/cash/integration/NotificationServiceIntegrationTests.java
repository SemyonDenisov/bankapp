package ru.yandex.cash.integration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.cash.KafkaTestConfig;
import ru.yandex.cash.model.Notification;
import ru.yandex.cash.service.NotificationService;

import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"notification.cash-microservice"})
@ActiveProfiles("test")
@Import(KafkaTestConfig.class)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class NotificationServiceIntegrationTests {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private KafkaTemplate<String, Notification> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @BeforeAll
    static void init(@Autowired EmbeddedKafkaBroker broker) {
        System.setProperty("spring.kafka.bootstrap-servers", broker.getBrokersAsString());
    }

    @Test
    void testSendNotification() {
        String expectedMessage = "Test message";
        String expectedEmail = "test@mail.com";

        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(expectedEmail, null);

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        notificationService.sendNotification(expectedMessage);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "testGroup", "true", embeddedKafkaBroker
        );

        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Notification.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, Object> consumer = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>()
        ).createConsumer();

        consumer.subscribe(Pattern.compile("notification.*"));

        ConsumerRecord<String, Object> record = KafkaTestUtils.getSingleRecord(
                consumer, "notification.cash-microservice"
        );
        assertThat(record).isNotNull();
        assertThat(record.value()).isInstanceOf(Notification.class);
        var castedRecord = (Notification) record.value();
        assertThat(castedRecord.getEmail()).isEqualTo(expectedEmail);
        assertThat(castedRecord.getMessage()).isEqualTo(expectedMessage);
    }
}
