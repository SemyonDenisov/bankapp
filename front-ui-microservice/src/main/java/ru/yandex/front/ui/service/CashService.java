package ru.yandex.front.ui.service;

import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.front.ui.model.Currency;

@Service
public class CashService {

    RestTemplate restTemplate;

    public CashService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean withdraw(Currency currency, double amount) {
        return changeBalance("http://cash-microservice/withdraw", currency, amount);
    }

    public boolean put(Currency currency, double amount) {
        return changeBalance("http://cash-microservice/put", currency, amount);
    }

    public boolean changeBalance(String url, Currency currency, double amount) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("currency", currency)
                .queryParam("amount", amount);

        String urlWithParams = builder.toUriString();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                urlWithParams,
                HttpMethod.POST,
                entity,
                Void.class
        );
        return response.getStatusCode() == HttpStatus.OK;
    }
}
