package ru.yandex.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.yandex.account.model.*;
import ru.yandex.account.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts() {
        return new ResponseEntity<>(accountService.getAccountsByEmail("1"), HttpStatus.OK);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Void> createAccount(@RequestBody NewAccountDto accountDto) {
        var principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account newAccount = new Account(accountDto, principal);
        accountService.saveAccount(newAccount);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/accounts")
    public ResponseEntity<Void> deleteAccount(AccountDto account) {
        accountService.deleteAccount(account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accounts/withdraw-money")
    public ResponseEntity<Void> withdraw(@RequestBody AccountDto accountDto, Double amount) {
        accountService.changeBalance(accountDto, Operation.WITHDRAW, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accounts/put-money")
    public ResponseEntity<Void> put(@RequestBody AccountDto accountDto, Double amount) {
        accountService.changeBalance(accountDto, Operation.PUT, amount);
        return ResponseEntity.ok().build();
    }

}