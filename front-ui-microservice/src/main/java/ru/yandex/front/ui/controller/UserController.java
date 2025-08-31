package ru.yandex.front.ui.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.front.ui.service.AccountService;
import ru.yandex.front.ui.service.TransferService;

@Controller
@RequestMapping("/user")
public class UserController {
    TransferService transferService;
    AccountService accountService;

    public UserController(TransferService transferService, AccountService accountService) {
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @PostMapping("/{login}/editPassword")
    public void changePassword(@PathVariable String login, String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            accountService.changePassword(password,confirmPassword);
        } else {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }


    @PostMapping("/{login}/cash")
    public void cash(@PathVariable String login,String action) {

    }

    @PostMapping("/{login}/transfer")
    public void transfer(@PathVariable String login,String action) {

    }
}

