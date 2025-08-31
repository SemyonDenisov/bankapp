package ru.yandex.exchange.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CurrencyConversionRequest {
    private Currency from;
    private Currency to;
    private double amount;
}
