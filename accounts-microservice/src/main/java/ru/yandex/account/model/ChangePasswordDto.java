package ru.yandex.account.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChangePasswordDto {
    @Length(min = 1, max = 30, message = "Bad password format")
    private String password;
    private String confirmPassword;
}
