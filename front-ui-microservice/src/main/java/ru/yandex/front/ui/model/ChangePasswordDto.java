package ru.yandex.front.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordDto {
    private String password;
    private String confirmPassword;
}
