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
    ClientCredentialService clientCredentialService;

    public CurrencyService(RestTemplate restTemplate, ClientCredentialService clientCredentialService) {
        this.restTemplate = restTemplate;
        this.clientCredentialService = clientCredentialService;
    }

    @Scheduled(fixedRate = 1000)
    public void generateCurrency() {
        List<CurrencyQuotation> quotations = new ArrayList<>();
        Arrays.stream(Currency.values())
                .forEach(currency -> quotations.add(new CurrencyQuotation(currency, generateExchangeRate(currency))));

        var token = clientCredentialService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<List<CurrencyQuotation>> entity = new HttpEntity<>(quotations, headers);

//
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//        var decision = restTemplate.exchange("http://blocker-microservice/block", HttpMethod.GET,entity,Boolean.class);

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
}
