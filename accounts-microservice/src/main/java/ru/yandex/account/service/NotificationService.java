package ru.yandex.account.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.account.model.Notification;
import ru.yandex.account.model.User;

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
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var notification = new Notification(user.getEmail(), message);
        kafkaTemplate.send("notification." + applicationName, notification);
    }
}
