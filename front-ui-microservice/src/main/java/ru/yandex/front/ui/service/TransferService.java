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
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.model.CurrencyQuotation;
import ru.yandex.front.ui.model.TransferRequest;

import java.util.List;

@Service
public class TransferService {

    RestTemplate restTemplate;
    CircuitBreaker circuitBreaker;
    Retry retry;


    public TransferService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("transfer-microservice");
        retry = Retry.ofDefaults("transfer-microservice");
    }


    public boolean selfTransfer(Currency from, Currency to, double amount) {
        TransferRequest transferRequest = new TransferRequest(from, to, amount, "");
        return postRequest(transferRequest,"http://transfer-microservice/transfer",HttpMethod.POST);
    }

    public boolean transferToAnother(Currency from, Currency to, double amount, String login) {
        TransferRequest transferRequest = new TransferRequest(from, to, amount, login);
        return postRequest(transferRequest,"http://transfer-microservice/transfer",HttpMethod.POST);
    }

    public boolean postRequest(TransferRequest transferRequest, String url, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity<TransferRequest>  entity = new HttpEntity<>(transferRequest, headers);
        return Boolean.TRUE.equals(retry.executeSupplier(() ->
                circuitBreaker.executeSupplier(() ->
                        restTemplate.exchange(url, method, entity, Boolean.class).getBody())));

    }



}
