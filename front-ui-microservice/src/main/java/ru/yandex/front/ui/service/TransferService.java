package ru.yandex.front.ui.service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.model.TransferRequest;

@Service
public class TransferService {

    RestTemplate restTemplate;

    public TransferService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
        return Boolean.TRUE.equals(restTemplate.exchange(url, method, entity, Boolean.class).getBody());

    }



}
