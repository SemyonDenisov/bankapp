package ru.yandex.cash.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.cash.model.Currency;
import ru.yandex.cash.service.CashService;
import ru.yandex.cash.service.NotificationService;

@RestController
public class CashController {

    CashService cashService;
    NotificationService notificationService;

    public CashController(CashService cashService, NotificationService notificationService) {
        this.cashService = cashService;
        this.notificationService = notificationService;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestParam("amount") double amount, @RequestParam("currency") Currency currency) {
        if(cashService.withdraw(currency, amount)) {
            notificationService.sendNotification("cash service: withdraw success");
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/put")
    public ResponseEntity<Void> put(@RequestParam("amount") double amount, @RequestParam("currency") Currency currency) {
        if(cashService.put(currency, amount)){
            notificationService.sendNotification("cash service: put success");
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
}
