package ru.yandex.transfer.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.transfer.model.Notification;

@Service
@Slf4j
public class NotificationService {

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Notification> kafkaTemplate;
    CircuitBreaker circuitBreaker;
    Retry retry;

    @Value("${spring.application.name}")
    private String applicationName;

    public NotificationService(RestTemplate restTemplate, KafkaTemplate<String, Notification> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("notifications-microservice");
        retry = Retry.ofDefaults("notifications-microservice");
    }

    public void sendNotification(String message) {
        var email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var notification = new Notification(email, message);
        kafkaTemplate.send("notification." + applicationName, notification);
    }
}
