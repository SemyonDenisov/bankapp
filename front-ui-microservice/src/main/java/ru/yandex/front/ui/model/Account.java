package ru.yandex.front.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Currency currency;
    private Boolean exists;
    private Double balance;

    public boolean isExists() {
        return exists;
    }
}
