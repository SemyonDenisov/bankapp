package ru.yandex.exchange.generator;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import ru.yandex.exchange.generator.model.CurrencyQuotation;

import java.util.Map;

@TestConfiguration
public class KafkaTestConfig {

    @Bean
    public ProducerFactory<String, CurrencyQuotation> testProducerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> props = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, CurrencyQuotation> testKafkaTemplate(ProducerFactory<String, CurrencyQuotation> pf) {
        return new KafkaTemplate<>(pf);
    }
}
