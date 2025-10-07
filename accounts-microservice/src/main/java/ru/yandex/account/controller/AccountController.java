package ru.yandex.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.yandex.account.model.*;
import ru.yandex.account.service.AccountService;
import ru.yandex.account.service.NotificationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class AccountController {

    private final NotificationService notificationService;

    private final AccountService accountService;

    public AccountController(AccountService accountService, NotificationService notificationService) {
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts() {
        var principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(accountService.getAccountsByEmail(principal.getEmail()), HttpStatus.OK);
    }


    @PostMapping("/accounts/put")
    public ResponseEntity<Void> putCash(@RequestParam(name = "currency") Currency currency,
                                        @RequestParam(name = "amount") Double amount,
                                        @RequestParam(name = "login", required = false) String login) {
        if (login != null && !login.isEmpty() && accountService.putAnother(currency, amount, login)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else if (accountService.putSelf(currency, amount)) {
            notificationService.sendNotification("money put success");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            notificationService.sendNotification("money put error");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/accounts/withdraw")
    public ResponseEntity<Void> withdrawCash(@RequestParam(name = "currency") Currency currency, @RequestParam(name = "amount") Double amount) {
        if (accountService.withDraw(currency, amount)) {
            notificationService.sendNotification("money withdraw success");
            return ResponseEntity.ok().build();
        } else {
            notificationService.sendNotification("money withdraw error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}