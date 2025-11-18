package ru.yandex.exchange.integration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.exchange.KafkaTestConfig;
import ru.yandex.exchange.model.Currency;
import ru.yandex.exchange.model.CurrencyQuotation;
import ru.yandex.exchange.service.CurrencyStoreService;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@EmbeddedKafka(partitions = 1, topics = "exchange.test")
@Import(KafkaTestConfig.class)
@ActiveProfiles("test")
public class CurrencyStoreServiceIntegrationTests {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaTemplate<String, CurrencyQuotation> kafkaTemplate;

    @Autowired
    private CurrencyStoreService currencyStoreService;

    @BeforeAll
    static void init(@Autowired EmbeddedKafkaBroker broker) {
        System.setProperty("spring.kafka.bootstrap-servers", broker.getBrokersAsString());
    }


    @Test
    void testKafkaListener() throws InterruptedException {
        CurrencyQuotation msg = new CurrencyQuotation(Currency.RUB, 2);
        kafkaTemplate.send("exchange.test", msg);
        kafkaTemplate.flush();

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    var rubQuotation = currencyStoreService.getQuotations().stream().filter(quotation -> quotation.getCurrency().equals(Currency.RUB)).findFirst().orElse(null);
                    assertEquals(2, rubQuotation.getRate());
                });
    }
}
