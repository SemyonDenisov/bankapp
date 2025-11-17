package ru.yandex.exchange.generator.integration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.exchange.generator.KafkaTestConfig;
import ru.yandex.exchange.generator.TestSecurityConfig;
import ru.yandex.exchange.generator.model.Currency;
import ru.yandex.exchange.generator.model.CurrencyQuotation;
import ru.yandex.exchange.generator.service.CurrencyService;


import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1, topics = {"exchange.RUB"})
@ActiveProfiles("test")
@Import({KafkaTestConfig.class})
class CurrencyServiceIntegrationTests {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private KafkaTemplate<String, CurrencyQuotation> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${spring.embedded.kafka.brokers}")
    String broker;

    @Test
    void testSendNotification() {
        currencyService.generateCurrency();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "testGroup", "true", embeddedKafkaBroker
        );

        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CurrencyService.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, Object> consumer = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>()
        ).createConsumer();

        consumer.subscribe(Pattern.compile("exchange.*"));

        var records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));
        var record = records.records("exchange.RUB").iterator().next();
        assertThat(record).isNotNull();
        assertThat(record.value()).isInstanceOf(CurrencyQuotation.class);
        var castedRecord = (CurrencyQuotation) record.value();
        assertThat(castedRecord.getCurrency()).isEqualTo(Currency.RUB);
        assertThat(castedRecord.getRate()).isEqualTo(1);
    }
}
