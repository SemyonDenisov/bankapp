package ru.yandex.exchange.generator.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.exchange.generator.service.CurrencyService;

@RestController
public class CurrencyController {

    CurrencyService cashService;

    public CurrencyController(CurrencyService cashService) {
        this.cashService = cashService;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestParam("amount") double amount) {
        String number = "777";
        cashService.withdraw(number, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/put")
    public ResponseEntity<Void> put(@RequestParam("amount") double amount) {
        String number = "777";
        cashService.put(number, amount);
        return ResponseEntity.ok().build();
    }
}
