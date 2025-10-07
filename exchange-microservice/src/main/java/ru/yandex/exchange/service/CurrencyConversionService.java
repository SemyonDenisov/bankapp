package ru.yandex.exchange.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.exchange.model.Currency;
import ru.yandex.exchange.model.CurrencyConversionRequest;
import ru.yandex.exchange.model.CurrencyConversionResponse;

@Service
public class CurrencyConversionService {


    RestTemplate restTemplate;
    CurrencyStoreService currencyStoreService;

    public CurrencyConversionService(RestTemplate restTemplate, CurrencyStoreService currencyStoreService) {
        this.restTemplate = restTemplate;
        this.currencyStoreService = currencyStoreService;
    }

    public CurrencyConversionResponse conversation(CurrencyConversionRequest currencyConversionRequest) {
        double amount = exchange(currencyConversionRequest.getFrom(), currencyConversionRequest.getTo(), currencyConversionRequest.getAmount());
        if (!currencyConversionRequest.getTo().equals(Currency.RUB)&&currencyConversionRequest.getFrom()!=Currency.RUB) {
            amount = exchange(Currency.RUB, currencyConversionRequest.getTo(), amount);
        }
        return new CurrencyConversionResponse(currencyConversionRequest.getFrom(), currencyConversionRequest.getTo(), amount);
    }


    public double exchange(Currency from, Currency to, Double amount) {
        if (from.equals(Currency.RUB)) {
            double rate = currencyStoreService.getRate(to).orElseThrow(RuntimeException::new);
            return amount * (1 / rate);
        } else {
            double rate = currencyStoreService.getRate(from).orElseThrow(RuntimeException::new);
            return amount * rate;
        }
    }

}
