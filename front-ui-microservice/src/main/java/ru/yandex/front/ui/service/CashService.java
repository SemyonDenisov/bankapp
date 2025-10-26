package ru.yandex.front.ui.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.front.ui.model.Account;
import ru.yandex.front.ui.model.Currency;

import java.util.List;

@Service
public class CashService {

    RestTemplate restTemplate;
    CircuitBreaker circuitBreaker;
    Retry retry;

    public CashService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("cash-microservice");
        retry = Retry.ofDefaults("cash-microservice");
    }

    public boolean withdraw(Currency currency, double amount) {
        return changeBalance("http://api-gateway/cash/withdraw", currency, amount);
    }

    public boolean put(Currency currency, double amount) {

        return changeBalance("http://api-gateway/cash/put", currency, amount);
    }

    public boolean changeBalance(String url, Currency currency, double amount) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("currency", currency)
                .queryParam("amount", amount);

        String urlWithParams = builder.toUriString();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return retry.executeSupplier(() -> circuitBreaker.executeSupplier(() -> restTemplate.exchange(
                urlWithParams,
                HttpMethod.POST,
                entity,
                Void.class
        ))).getStatusCode().is2xxSuccessful();
    }
}
