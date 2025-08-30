package ru.yandex.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.account.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
