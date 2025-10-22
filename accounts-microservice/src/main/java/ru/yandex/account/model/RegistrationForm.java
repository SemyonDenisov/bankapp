package ru.yandex.account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.account.validation.Adult;

import java.time.LocalDate;


@Data
@AllArgsConstructor
public class RegistrationForm {
    @NotNull
    @Email(message = "Bad email format")
    private String login;
    @NotNull

    @Length(min = 1, max = 30, message = "Bad password format")
    private String password;

    @JsonProperty("confirm_password")
    private String confirmPassword;

    @Length(min = 1, max = 30, message = "Bad username format")
    @JsonProperty("name")
    private String name;

    @Adult
    private LocalDate birthdate;
}
