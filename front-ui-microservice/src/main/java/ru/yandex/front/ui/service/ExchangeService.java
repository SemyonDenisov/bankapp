package ru.yandex.front.ui.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.model.Account;
import ru.yandex.front.ui.model.CurrencyQuotation;

import java.util.List;

@Service
public class ExchangeService {

    ClientCredentialService clientCredentialService;
    RestTemplate restTemplate;
    CircuitBreaker circuitBreaker;
    Retry retry;

    public ExchangeService(ClientCredentialService clientCredentialService, RestTemplate restTemplate) {
        this.clientCredentialService = clientCredentialService;
        this.restTemplate = restTemplate;
        circuitBreaker = CircuitBreaker.ofDefaults("exchange-microservice");
        retry = Retry.ofDefaults("exchange-microservice");
    }

    public List<CurrencyQuotation> getRates() {
        var token = clientCredentialService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return retry.executeSupplier(() ->
                circuitBreaker.executeSupplier(() ->
                        restTemplate.exchange("http://api-gateway/exchange/rates",
                                HttpMethod.GET, entity,
                                new ParameterizedTypeReference<List<CurrencyQuotation>>() {
                                }).getBody()));
    }
}
