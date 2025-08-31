package ru.yandex.account.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.yandex.account.dao.AccountRepository;
import ru.yandex.account.dao.UserRepository;
import ru.yandex.account.model.Account;
import ru.yandex.account.model.AccountDto;
import ru.yandex.account.model.Operation;
import ru.yandex.account.model.User;

import java.util.List;
import java.util.Objects;

@Service
public class AccountService {
    AccountRepository accountRepository;
    UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<AccountDto> getAccountsByEmail(String email) {
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        var accounts = accountRepository.findByUser(user);
        return accounts.stream().map(AccountDto::new).toList();
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    public void deleteAccount(AccountDto accountDto) {
        var account = accountRepository.findByNumber(accountDto.getNumber());
        if (account == null) {
            throw new UsernameNotFoundException(accountDto.getNumber());
        }
        accountRepository.delete(account);
    }

    public void changeBalance(String number, Operation operation, Double amount) {
        Account account = accountRepository.findByNumber(number);
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var existsNumber = this.getAccountsByEmail(user.getEmail()).stream().filter(account1 -> Objects.equals(account1.getNumber(), number)).count();
        if (existsNumber > 0) {
            if (Operation.WITHDRAW == operation) {
                account.setBalance(account.getBalance() - amount);
            } else if (Operation.PUT == operation) {
                account.setBalance(account.getBalance() + amount);
            }
            accountRepository.save(account);
        }
        else {
            throw new UsernameNotFoundException(account.getNumber());
        }
    }
}
