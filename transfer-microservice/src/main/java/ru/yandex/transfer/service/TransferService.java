package ru.yandex.transfer.service;

import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.transfer.model.AccountInfo;
import ru.yandex.transfer.model.CurrencyConversionResponse;
import ru.yandex.transfer.model.TransferRequest;

@Service
public class TransferService {

    RestTemplate restTemplate;

    public TransferService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void transfer(TransferRequest transferRequest) {
        if(transferRequest.getFrom().equals(transferRequest.getTo())) {
            throw new RuntimeException("From and To are the same");
        }
        var fromAccountInfo = getRequest("http://accounts-microservice/accounts/" + transferRequest.getFrom(), AccountInfo.class);
        var toAccountInfo = getRequest("http://accounts-microservice/accounts/" + transferRequest.getTo(), AccountInfo.class);

        var amountToWithDraw = transferRequest.getAmount();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://exchange-microservice/conversion")
                .queryParam("from", fromAccountInfo.getCurrency())
                .queryParam("to", toAccountInfo.getCurrency())
                .queryParam("amount", transferRequest.getAmount());

        String urlWithParams = builder.toUriString();

        var currencyConversionResponse = getRequest(urlWithParams, CurrencyConversionResponse.class);
        var amountToPut = currencyConversionResponse.getAmount();

        builder = UriComponentsBuilder.fromUriString("http://accounts-microservice/accounts/withdraw-money")
                .queryParam("number", fromAccountInfo.getNumber())
                .queryParam("amount", amountToWithDraw);
        urlWithParams = builder.toUriString();
        postRequest(urlWithParams, Void.class);

        builder = UriComponentsBuilder.fromUriString("http://accounts-microservice/accounts/put-money")
                .queryParam("number", toAccountInfo.getNumber())
                .queryParam("amount", amountToPut);
        urlWithParams = builder.toUriString();
        postRequest(urlWithParams, Void.class);
    }

    public <T> T getRequest(String url, Class<T> tClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, tClass).getBody();
    }

    public <T> T postRequest(String url, Class<T> tClass) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, tClass).getBody();
    }

}
