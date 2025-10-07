package ru.yandex.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.account.model.Account;
import ru.yandex.account.model.Currency;
import ru.yandex.account.model.User;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByUser(User user);


    Optional<Account> findByUserAndCurrency(User user, Currency currency);
}
