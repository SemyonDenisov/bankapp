package ru.yandex.account.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class NotificationService {

    private final RestTemplate restTemplate;

    CircuitBreaker circuitBreaker;
    Retry retry;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("notifications-microservice");
        retry = Retry.ofDefaults("notifications-microservice");
    }

    public void sendNotification(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        HttpEntity<String> entity = new HttpEntity<>(message, headers);
        try {
            retry.executeSupplier(() ->
                    circuitBreaker.executeSupplier(() ->
                            restTemplate.postForObject("http://notifications-microservice/notify", entity, String.class)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
