package ru.yandex.front.ui.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account {
    private Currency currency;
    private Boolean exists;
    private Double balance;

    public boolean isExists() {
        return exists;
    }
}
