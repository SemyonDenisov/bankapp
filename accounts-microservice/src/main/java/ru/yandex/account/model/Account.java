package ru.yandex.account.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number")
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "balance")
    @Setter
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Account(NewAccountDto newAccountDto, User user) {
        this.balance = 0.0;
        this.currency = newAccountDto.getCurrency();
        this.number = UUID.randomUUID().toString();
        this.user = user;
    }
}
