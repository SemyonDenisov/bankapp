package ru.yandex.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.account.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
