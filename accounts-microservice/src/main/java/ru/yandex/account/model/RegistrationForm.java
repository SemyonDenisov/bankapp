package ru.yandex.account.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.account.validation.Adult;


@Data
public class RegistrationForm {
    @NotNull
    @Email(message = "Bad email format")
    private String email;
    @NotNull

    @Length(min = 1, max = 30, message = "Bad password format")
    private String password;

    private String confirmPassword;

    @Length(min = 1, max = 30, message = "Bad username format")
    private String username;

    @Adult
    private String birthday;
}
