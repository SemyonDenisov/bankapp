package ru.yandex.account.model;

import lombok.Data;

@Data
public class AccountDto {

    private String number;

    private Currency currency;

    private Double balance;

    public AccountDto(Account account) {
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.number = account.getNumber();
    }

}
