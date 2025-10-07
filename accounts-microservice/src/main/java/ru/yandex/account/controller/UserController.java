package ru.yandex.account.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.yandex.account.model.ChangePasswordDto;
import ru.yandex.account.model.UpdateUserDto;
import ru.yandex.account.model.User;
import ru.yandex.account.model.UserDto;
import ru.yandex.account.service.AccountService;
import ru.yandex.account.service.NotificationService;
import ru.yandex.account.service.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    UserController(UserService userService, AccountService accountService,NotificationService notificationService) {
        this.userService = userService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @PostMapping("/edit")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UpdateUserDto updates) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean userUpdate = false;
        if (updates.getBirthday() != null) {
            user.setBirthday(updates.getBirthday());
            userUpdate = true;
        }
        if (updates.getUsername() != null && !updates.getUsername().replace(" ", "").isEmpty()) {
            user.setUsername(updates.getUsername());
            userUpdate = true;
        }
        accountService.updateAccounts(updates.getSelectedCurrencies());
        if (userUpdate) {
            userService.save(user);
        }
        notificationService.sendNotification("edit user success");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDto changePassword) {
        if (!Objects.equals(changePassword.getPassword(), changePassword.getConfirmPassword())) {
            return ResponseEntity.badRequest().build();
        }
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPassword(changePassword.getPassword());
        userService.save(user);
        notificationService.sendNotification("change password success");
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
        notificationService.sendNotification("delete user success");
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getUsers().stream().map(user -> new UserDto(user.getEmail(),user.getUsername())).toList());
    }
}
