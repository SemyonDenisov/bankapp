package ru.yandex.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginForm {
    private String email;
    private String password;
}
