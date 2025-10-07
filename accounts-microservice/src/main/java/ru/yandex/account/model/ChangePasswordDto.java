package ru.yandex.account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
    //@Length(min = 1, max = 30, message = "Bad password format")
    private String password;
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
