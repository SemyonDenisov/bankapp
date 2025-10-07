package ru.yandex.account.model;

import lombok.Data;

@Data
public class AccountDto {

    private Boolean exists;

    private Currency currency;

    private Double balance;

    public AccountDto(Account account, Boolean exists) {
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.exists = exists;
    }

}
