package ru.yandex.exchange.generator.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
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
    CircuitBreaker circuitBreaker;
    Retry retry;
    KafkaTemplate<String, CurrencyQuotation> kafkaTemplate;

    public CurrencyService(RestTemplate restTemplate,
                           KafkaTemplate<String, CurrencyQuotation> kafkaTemplate) {
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("exchange-microservice");
        retry = Retry.ofDefaults("exchange-microservice");
        this.kafkaTemplate = kafkaTemplate;
    }

//    @Scheduled(fixedRate = 1000)
//    public void generateCurrency() {
//        List<CurrencyQuotation> quotations = new ArrayList<>();
//        Arrays.stream(Currency.values())
//                .forEach(currency -> quotations.add(new CurrencyQuotation(currency, generateExchangeRate(currency))));
//
//        var token = clientCredentialService.getToken();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//        HttpEntity<List<CurrencyQuotation>> entity = new HttpEntity<>(quotations, headers);
//
//
//        HttpEntity<Void> blockerEntity = new HttpEntity<>(headers);
//        boolean decision;
//
//        try {
//            decision = retry.executeSupplier(() ->
//                    circuitBreaker.executeSupplier(() ->
//                            restTemplate.exchange(
//                                    "http://blocker-microservice/block",
//                                    HttpMethod.GET,
//                                    blockerEntity,
//                                    Boolean.class
//                            ).getBody()
//                    )
//            );
//        } catch (Exception e) {
//            log.warn("block service not available", e);
//            decision = false;
//        }
//
//        if (!decision) {
//            retry.executeSupplier(() ->
//                    circuitBreaker.executeSupplier(() -> restTemplate
//                            .postForEntity("http://exchange-microservice/update-quotations", entity, Void.class)));
//        }
//
//
//    }

    public double generateExchangeRate(Currency currency) {
        if (currency == Currency.USD) {
            return ThreadLocalRandom.current().nextDouble(90, 110);
        }
        if (currency == Currency.EUR) {
            return ThreadLocalRandom.current().nextDouble(90, 110);
        }
        return 1.0;
    }

    @Scheduled(fixedRate = 1000)
    public void generateCurrency() {
        List<CurrencyQuotation> quotations = new ArrayList<>();
        Arrays.stream(Currency.values())
                .forEach(currency -> quotations.add(new CurrencyQuotation(currency, generateExchangeRate(currency))));

        quotations.forEach(quotation -> {
            kafkaTemplate.send("exchange." + quotation.getCurrency(), quotation);
        });



//        var token = clientCredentialService.getToken();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//        HttpEntity<List<CurrencyQuotation>> entity = new HttpEntity<>(quotations, headers);
//
//
//        HttpEntity<Void> blockerEntity = new HttpEntity<>(headers);
//        boolean decision;
//
//        try {
//            decision = retry.executeSupplier(() ->
//                    circuitBreaker.executeSupplier(() ->
//                            restTemplate.exchange(
//                                    "http://blocker-microservice/block",
//                                    HttpMethod.GET,
//                                    blockerEntity,
//                                    Boolean.class
//                            ).getBody()
//                    )
//            );
//        } catch (Exception e) {
//            log.warn("block service not available", e);
//            decision = false;
//        }
//
//        if (!decision) {
//            retry.executeSupplier(() ->
//                    circuitBreaker.executeSupplier(() -> restTemplate
//                            .postForEntity("http://exchange-microservice/update-quotations", entity, Void.class)));
//        }


    }
}
