package ru.yandex.exchange.service;

import org.springframework.stereotype.Service;
import ru.yandex.exchange.model.Currency;
import ru.yandex.exchange.model.CurrencyQuotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CurrencyStoreService {


    private final Map<Currency, Double> rates = new ConcurrentHashMap<>();

    public void updateRate(Currency currency, double rate) {
        rates.put(currency, rate);
    }

    public Optional<Double> getRate(Currency currency) {
        return Optional.ofNullable(rates.get(currency));
    }

    public List<CurrencyQuotation> getQuotations() {
        List<CurrencyQuotation> quotations = new ArrayList<>();
        rates.forEach((k, v) -> quotations.add(new CurrencyQuotation(k, v)));
        return quotations;
    }
}
