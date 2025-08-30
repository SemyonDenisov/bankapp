package ru.yandex.account.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.yandex.account.model.ChangePasswordDto;
import ru.yandex.account.model.UpdateUserDto;
import ru.yandex.account.model.User;
import ru.yandex.account.service.AccountService;
import ru.yandex.account.service.UserService;

import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping("/edit")
    public ResponseEntity<Void> updateUser(@Valid UpdateUserDto updates) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (updates.getBirthday() != null) {
            user.setBirthday(updates.getBirthday());
        }
        if (updates.getUsername() != null) {
            user.setUsername(updates.getUsername());
        }
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid ChangePasswordDto changePassword) {
        if (!Objects.equals(changePassword.getPassword(), changePassword.getConfirmPassword())) {
            return ResponseEntity.badRequest().build();
        }
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPassword(changePassword.getPassword());
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = {"/", ""})
    public ResponseEntity<Void> deleteUser() {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var accountsSize = accountService.getAccountsByEmail(user.getEmail()).size();
        if (accountsSize > 0) {
            throw new RuntimeException("Already have accounts");
        }
        userService.delete(user);
        return ResponseEntity.ok().build();
    }
}
