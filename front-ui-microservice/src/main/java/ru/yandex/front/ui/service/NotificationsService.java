package ru.yandex.front.ui.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NotificationsService {

    RestTemplate restTemplate;
    CircuitBreaker circuitBreaker;
    Retry retry;

    public NotificationsService( RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("notifications-microservice");
        retry = Retry.ofDefaults("notifications-microservice");
    }

    public List<String> getNotifications(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return retry.executeSupplier(() ->
                circuitBreaker.executeSupplier(() ->
                        restTemplate.exchange("http://api-gateway/notifications/old-notifications",
                                HttpMethod.GET, entity,
                                new ParameterizedTypeReference<List<String>>() {
                                }).getBody()));
    }
}
