package ru.yandex.front.ui.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.front.ui.model.Account;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.service.AccountService;
import ru.yandex.front.ui.service.TransferService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class FrontController {

    TransferService transferService;

    AccountService accountService;

    public FrontController(TransferService transferService,
                           AccountService accountService) {
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        var accounts = accountService.getAccounts();

        var currencies = accounts.stream().filter(Account::getExists).map(Account::getCurrency).toList();
        model.addAttribute("accounts", accounts);
        model.addAttribute("currency", currencies);
        model.addAttribute("currencyToSend", Currency.values());
        model.addAttribute("users", accountService.getUsers());
        model.addAttribute("jwtToken",SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        return "main";
    }
//
//    @GetMapping("/signup")
//    public String signUpPage(@RequestParam("login") String login,
//                                 @RequestParam("password") String password,
//                                 @RequestParam("confirm_password")String confirmPassword,
//                                 @RequestParam("name") String name,
//                                 @RequestParam("birthdate") LocalDate birthdate,
//                                 Model model) {
//
//    }



}
