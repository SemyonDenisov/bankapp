package ru.yandex.front.ui.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.service.AccountService;
import ru.yandex.front.ui.service.CashService;
import ru.yandex.front.ui.service.TransferService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
    TransferService transferService;
    AccountService accountService;
    CashService cashService;

    public UserController(TransferService transferService,
                          AccountService accountService,
                          CashService cashService) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.cashService = cashService;
    }

    @PostMapping("/editPassword")
    public String changePassword(@RequestParam("password") String password,
                                 @RequestParam("confirm_password") String confirmPassword) {
        if (password.equals(confirmPassword)) {
            accountService.changePassword(password, confirmPassword);
        } else {
            throw new IllegalArgumentException("Passwords do not match");
        }
        return "redirect:/";
    }

    @PostMapping("/editUserAccounts")
    public String editAccount(@RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "birthdate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
                              @RequestParam(name = "account", required = false) List<Currency> selectedCurrencies) {
        if ((name != null && !name.replaceAll(" ", "").isEmpty()) ||
                birthdate != null
                || selectedCurrencies != null) {
            accountService.editAccount(name, birthdate, selectedCurrencies);
        }
        return "redirect:/";
    }


    @PostMapping("/cash")
    public String handleCashAction(
            @RequestParam("currency") Currency currency,
            @RequestParam("value") double value,
            @RequestParam("action") String action,
            Model model
    ) {
        if ("PUT".equals(action)) {
            cashService.put(currency, value);
        } else if ("GET".equals(action)) {
            cashService.withdraw(currency, value);
        } else {
            model.addAttribute("cashErrors", List.of("Неизвестное действие: " + action));
        }
        return "redirect:/";
    }


    @PostMapping("/transfer")
    public String transfer(@RequestParam(name = "from_currency") Currency currencyFrom,
                           @RequestParam(name = "to_currency") Currency currencyTo,
                           @RequestParam(name = "value") Double value,
                           @RequestParam(name = "to_login", defaultValue = "") String login,
                           RedirectAttributes redirectAttributes) {

        log.info("\n\n\n{}\n\n\n", login);
        if (login != null && !login.isEmpty()) {
            transferService.transferToAnother(currencyFrom, currencyTo, value, login);
        } else {
            transferService.selfTransfer(currencyFrom, currencyTo, value);
        }

        return "redirect:/";
    }
}

