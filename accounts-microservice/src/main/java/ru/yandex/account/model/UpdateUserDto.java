package ru.yandex.account.model;

import lombok.Data;
import ru.yandex.account.validation.Adult;

import java.time.LocalDate;

@Data
public class UpdateUserDto {

    private String username;

    @Adult
    private LocalDate birthday;
}
