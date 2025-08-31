package ru.yandex.transfer.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CurrencyConversionResponse {
    private Currency from;
    private Currency to;
    private double amount;
}
