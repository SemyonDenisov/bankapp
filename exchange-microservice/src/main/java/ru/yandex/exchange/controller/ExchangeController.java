package ru.yandex.exchange.controller;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.exchange.model.Currency;
import ru.yandex.exchange.model.CurrencyConversionRequest;
import ru.yandex.exchange.model.CurrencyConversionResponse;
import ru.yandex.exchange.model.CurrencyQuotation;
import ru.yandex.exchange.service.CurrencyConversionService;
import ru.yandex.exchange.service.CurrencyStoreService;

import java.util.List;

@RestController
public class ExchangeController {

    CurrencyConversionService currencyConversionService;
    CurrencyStoreService currencyStoreService;

    public ExchangeController(CurrencyConversionService currencyConversionService, CurrencyStoreService currencyStoreService) {
        this.currencyConversionService = currencyConversionService;
        this.currencyStoreService = currencyStoreService;
    }

    @GetMapping("/conversion")
    public ResponseEntity<CurrencyConversionResponse> conversion(@RequestParam(name = "from") Currency from,
                                                                 @RequestParam(name = "to") Currency to,
                                                                 @RequestParam(name = "amount") Double amount) {
        return new ResponseEntity<>(currencyConversionService.conversation(new CurrencyConversionRequest(from, to, amount)), HttpStatus.OK);
    }

    @PostMapping("/update-quotations")
    public ResponseEntity<Void> updateQuotations(@RequestBody List<CurrencyQuotation> quotations) {
        quotations.forEach(quotation -> currencyStoreService.updateRate(quotation.getCurrency(), quotation.getRate()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
