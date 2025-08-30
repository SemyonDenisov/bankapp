package ru.yandex.account.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.account.service.AccountService;
import ru.yandex.account.service.UserService;

@RestController
@RequestMapping("/")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/a")
    public String a() {
        return "aaaaa";
    }
}
