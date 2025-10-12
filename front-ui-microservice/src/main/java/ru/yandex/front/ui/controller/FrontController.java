package ru.yandex.front.ui.controller;


import org.springframework.http.HttpHeaders;
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
import ru.yandex.front.ui.model.CurrencyQuotation;
import ru.yandex.front.ui.model.RegistrationForm;
import ru.yandex.front.ui.service.AccountService;
import ru.yandex.front.ui.service.ClientCredentialService;
import ru.yandex.front.ui.service.ExchangeService;
import ru.yandex.front.ui.service.TransferService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class FrontController {

    TransferService transferService;

    AccountService accountService;

    ClientCredentialService clientCredentialService;

    ExchangeService exchangeService;

    public FrontController(TransferService transferService,
                           AccountService accountService,
                           ClientCredentialService clientCredentialService, ExchangeService exchangeService) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.clientCredentialService = clientCredentialService;
        this.exchangeService = exchangeService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        var accounts = accountService.getAccounts();

        var currencies = accounts.stream().filter(Account::getExists).map(Account::getCurrency).toList();
        model.addAttribute("accounts", accounts);
        model.addAttribute("currency", currencies);
        model.addAttribute("currencyToSend", Currency.values());
        var users = accountService.getUsers();
        model.addAttribute("users", users);
        model.addAttribute("jwtToken", SecurityContextHolder.getContext().getAuthentication().getDetails().toString());
        model.addAttribute("clientCredentialToken", clientCredentialService.getToken());
        return "main";
    }


    @GetMapping("/signup")
    public String signUpPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam("confirm_password") String confirmPassword,
            @RequestParam String name,
            @RequestParam String birthdate,
            Model model
    ) {
        List<String> errors = new ArrayList<>();

        if (!password.equals(confirmPassword)) {
            errors.add("Пароли не совпадают");
        }

        if (login.isBlank() || password.isBlank() || name.isBlank()) {
            errors.add("Все поля обязательны для заполнения");
        }

        LocalDate birthDateParsed;
        try {
            birthDateParsed = LocalDate.parse(birthdate);
        } catch (Exception e) {
            errors.add("Неверный формат даты рождения");
            birthDateParsed = null;
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("login", login);
            model.addAttribute("name", name);
            model.addAttribute("birthdate", birthdate);
            return "signup";
        }
        accountService.registration(new RegistrationForm(login, password, confirmPassword, name, birthDateParsed));
        return "redirect:/login";
    }

    @GetMapping("/rates")
    public ResponseEntity<List<CurrencyQuotation>> getRates() {
        return ResponseEntity.ok(exchangeService.getRates());
    }


}
