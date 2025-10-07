package ru.yandex.front.ui.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @JsonProperty("from_currency")
    private Currency fromCurrency;
    @JsonProperty("to_currency")
    private Currency toCurrency;
    private Double amount;
    private String login;
}
