package ru.yandex.transfer.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.transfer.model.CurrencyConversionResponse;

@Service
@Slf4j
public class TransferService {

    ClientCredentialService clientCredentialService;

    CircuitBreaker circuitBreaker;
    Retry retry;

    private final RestTemplate restTemplate;

    public TransferService(RestTemplate restTemplate, ClientCredentialService clientCredentialService) {
        this.clientCredentialService = clientCredentialService;
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("transfer-microservice");
        retry = Retry.ofDefaults("transfer-microservice");
    }

    public boolean transfer(ru.yandex.front.ui.model.TransferRequest transferRequest) {
        if (transferRequest.getFromCurrency().equals(transferRequest.getToCurrency())
                && (transferRequest.getLogin() == null || transferRequest.getLogin().isEmpty())) {
            log.info("hereeeeeeeeeeeeeeeeeeeeeeee");
            return false;
        }

        var userToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        var serviceToken = clientCredentialService.getToken();


        var amountToWithDraw = transferRequest.getAmount();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://api-gateway/exchange/conversion")
                .queryParam("from", transferRequest.getFromCurrency())
                .queryParam("to", transferRequest.getToCurrency())
                .queryParam("amount", transferRequest.getAmount());

        String urlWithParams = builder.toUriString();

        var currencyConversionResponse = getRequest(urlWithParams, CurrencyConversionResponse.class, serviceToken);
        var amountToPut = currencyConversionResponse.getAmount();

        builder = UriComponentsBuilder.fromUriString("http://api-gateway/accounts/accounts/withdraw")
                .queryParam("currency", transferRequest.getFromCurrency())
                .queryParam("amount", amountToWithDraw);
        urlWithParams = builder.toUriString();
        postRequest(urlWithParams, Void.class, userToken);

        builder = UriComponentsBuilder.fromUriString("http://api-gateway/accounts/accounts/put")
                .queryParam("currency", transferRequest.getToCurrency())
                .queryParam("amount", amountToPut)
                .queryParam("login", transferRequest.getLogin());
        urlWithParams = builder.toUriString();
        log.info("\n{}\n", serviceToken);
        log.info("\n{}\n", transferRequest.getLogin());
        postRequest(urlWithParams, Void.class, userToken);

        return true;
    }

    public <T> T getRequest(String url, Class<T> tClass, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return retry.executeSupplier(() ->
                circuitBreaker.executeSupplier(() -> {
                    var a = restTemplate.exchange(url, HttpMethod.GET, entity, tClass);
                    return a.getBody();
                }));

    }

    public <T> T postRequest(String url, Class<T> tClass, String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return retry.executeSupplier(() ->
                circuitBreaker.executeSupplier(() ->
                        restTemplate.exchange(url, HttpMethod.POST, entity, tClass).getBody()));
    }

}
