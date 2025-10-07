package ru.yandex.front.ui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    private String login;
    private String password;
    @JsonProperty("confirm_password")
    private String confirmPassword;
    private String name;
    private LocalDate birthdate;
}
