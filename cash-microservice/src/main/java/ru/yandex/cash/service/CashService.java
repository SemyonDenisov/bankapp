package ru.yandex.cash.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.cash.model.Currency;

@Service
public class CashService {

    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public CashService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("cash-microservice");
        retry = Retry.ofDefaults("cash-microservice");
    }

    public boolean withdraw(Currency currency, double amount) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://accounts-microservice/accounts/withdraw")
                .queryParam("currency", currency)
                .queryParam("amount", amount);

        String urlWithParams = builder.toUriString();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = retry.executeSupplier(() ->
                    circuitBreaker.executeSupplier(() ->
                            restTemplate.exchange(
                                    urlWithParams,
                                    HttpMethod.POST,
                                    entity,
                                    Void.class
                            )));
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean put(Currency currency, double amount) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://accounts-microservice/accounts/put")
                .queryParam("currency", currency)
                .queryParam("amount", amount);

        String urlWithParams = builder.toUriString();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = retry.executeSupplier(() ->
                    circuitBreaker.executeSupplier(() ->
                            restTemplate.exchange(
                                    urlWithParams,
                                    HttpMethod.POST,
                                    entity,
                                    Void.class
                            )));
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean changeBalance(String url, Currency currency, double amount) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("currency", currency)
                .queryParam("amount", amount);

        String urlWithParams = builder.toUriString();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = retry.executeSupplier(() ->
                    circuitBreaker.executeSupplier(() ->
                            restTemplate.exchange(
                                    urlWithParams,
                                    HttpMethod.POST,
                                    entity,
                                    Void.class
                            )));
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }

    }
}
