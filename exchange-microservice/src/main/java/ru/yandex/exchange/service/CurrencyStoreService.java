package ru.yandex.exchange.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.yandex.exchange.model.Currency;
import ru.yandex.exchange.model.CurrencyQuotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CurrencyStoreService {


    private final Map<Currency, Double> rates = new ConcurrentHashMap<>();

    private final AtomicLong lastUpdateTimestamp = new AtomicLong(System.currentTimeMillis());


    public CurrencyStoreService(MeterRegistry meterRegistry) {
        rates.put(Currency.USD, 86.0);
        rates.put(Currency.EUR, 86.0);
        rates.put(Currency.RUB, 1.0);
        Gauge.builder("exchange_rates_up", lastUpdateTimestamp, ts -> {
                    long elapsed = System.currentTimeMillis() - ts.get();
                    return elapsed <= 10_000 ? 1 : 0;
                })
                .description("1 if exchange rates updated in the last 10 seconds, 0 otherwise")
                .register(meterRegistry);
    }

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

    @KafkaListener(topicPattern = "exchange.*",groupId = "exchange-group")
    public void listen(CurrencyQuotation quotation, Acknowledgment ack) {
        try {
            updateRate(quotation.getCurrency(), quotation.getRate());
            lastUpdateTimestamp.set(System.currentTimeMillis());
            ack.acknowledge();
        }catch (Exception e) {
            ack.acknowledge();
            throw new RuntimeException(e);
        }
    }
}
