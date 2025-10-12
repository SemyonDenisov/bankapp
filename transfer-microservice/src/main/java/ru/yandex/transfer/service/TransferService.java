package ru.yandex.transfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.transfer.model.CurrencyConversionResponse;

@Service
public class TransferService {

    ClientCredentialService clientCredentialService;

    private final RestTemplate restTemplate;

    public TransferService(RestTemplate restTemplate, ClientCredentialService clientCredentialService) {
        this.clientCredentialService = clientCredentialService;
        this.restTemplate = restTemplate;
    }

    public boolean transfer(ru.yandex.front.ui.model.TransferRequest transferRequest) {
        if (transferRequest.getFromCurrency().equals(transferRequest.getToCurrency()) && transferRequest.getLogin().isEmpty()) {
            return false;
        }

        var userToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        var serviceToken = clientCredentialService.getToken();


        var amountToWithDraw = transferRequest.getAmount();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://exchange-microservice/conversion")
                .queryParam("from", transferRequest.getFromCurrency())
                .queryParam("to", transferRequest.getToCurrency())
                .queryParam("amount", transferRequest.getAmount());

        String urlWithParams = builder.toUriString();

        var currencyConversionResponse = getRequest(urlWithParams, CurrencyConversionResponse.class, serviceToken);
        var amountToPut = currencyConversionResponse.getAmount();

        builder = UriComponentsBuilder.fromUriString("http://accounts-microservice/accounts/withdraw")
                .queryParam("currency", transferRequest.getFromCurrency())
                .queryParam("amount", amountToWithDraw);
        urlWithParams = builder.toUriString();
        postRequest(urlWithParams, Void.class, userToken);

        builder = UriComponentsBuilder.fromUriString("http://accounts-microservice/accounts/put")
                .queryParam("currency", transferRequest.getToCurrency())
                .queryParam("amount", amountToPut)
                .queryParam("login", transferRequest.getLogin());
        urlWithParams = builder.toUriString();
        postRequest(urlWithParams, Void.class, serviceToken);
        return true;
    }

    public <T> T getRequest(String url, Class<T> tClass, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, tClass).getBody();
    }

    public <T> T postRequest(String url, Class<T> tClass, String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, tClass).getBody();
    }

}
