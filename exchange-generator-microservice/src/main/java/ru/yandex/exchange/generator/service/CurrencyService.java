package ru.yandex.exchange.generator.service;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.exchange.generator.model.Currency;
import ru.yandex.exchange.generator.model.CurrencyQuotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CurrencyService {

    RestTemplate restTemplate;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void generateCurrency() {
        List<CurrencyQuotation> quotations = new ArrayList<>();
        Arrays.stream(Currency.values())
                .forEach(currency -> quotations.add(new CurrencyQuotation(currency, generateExchangeRate(currency))));
        HttpEntity<List<CurrencyQuotation>> entity = new HttpEntity<>(quotations);
        ResponseEntity<Void> response = restTemplate
                .postForEntity("http://exchange-microservice/update-quotations", entity, Void.class);
    }

    public double generateExchangeRate(Currency currency) {
        if (currency == Currency.USD) {
            return ThreadLocalRandom.current().nextDouble(90, 110);
        }
        if (currency == Currency.EUR) {
            return ThreadLocalRandom.current().nextDouble(90, 110);
        }
        return 1.0;
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
