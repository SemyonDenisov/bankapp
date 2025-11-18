package ru.yandex.account;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka(partitions = 1, topics = {"notification.accounts-microservice"})
public abstract class BaseContractTest extends BaseTest {
    @BeforeAll
    static void init(@Autowired EmbeddedKafkaBroker broker) {
        System.setProperty("spring.kafka.bootstrap-servers", broker.getBrokersAsString());
    }
}
