package ru.yandex.exchange.generator.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CurrencyQuotation {
    private Currency currency;
    private double rate;
}
