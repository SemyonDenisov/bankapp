package ru.yandex.account.service;

import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CashService {

    RestTemplate restTemplate;

    public CashService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void withdraw(String number, double amount) {
        changeBalance("http://accounts-microservice/accounts/withdraw-money", number, amount);
    }

    public void put(String number, double amount) {
        changeBalance("http://accounts-microservice/accounts/put-money", number, amount);
    }

    public void changeBalance(String url, String number, double amount) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("number", number)
                .queryParam("amount", amount);

        String urlWithParams = builder.toUriString();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                urlWithParams,
                HttpMethod.POST,
                entity,
                Void.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error withdrawing account");
        }
    }
}
