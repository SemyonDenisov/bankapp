package ru.yandex.front.ui.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NotificationsService {

    ClientCredentialService clientCredentialService;
    RestTemplate restTemplate;
    CircuitBreaker circuitBreaker;
    Retry retry;


    public List<String> getNotifications(){
        var token = clientCredentialService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return retry.executeSupplier(() ->
                circuitBreaker.executeSupplier(() ->
                        restTemplate.exchange("http://notifications-service:8083/old-notifications",
                                HttpMethod.GET, entity,
                                new ParameterizedTypeReference<List<String>>() {
                                }).getBody()));
    }
}
