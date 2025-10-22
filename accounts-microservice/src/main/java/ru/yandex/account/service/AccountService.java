package ru.yandex.account.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.yandex.account.dao.AccountRepository;
import ru.yandex.account.dao.UserRepository;
import ru.yandex.account.model.*;
import ru.yandex.account.model.Currency;

import java.util.*;

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
        List<AccountDto> accountsDto = new ArrayList<>();
        var existedCurrency = accounts.stream().map(Account::getCurrency).toList();
        Arrays.stream(Currency.values()).forEach(currency -> {
            if (existedCurrency.contains(currency)) {
                var accountWithSpecCurrency = accounts.stream().filter(account -> account.getCurrency().equals(currency)).findFirst().get();
                accountsDto.add(new AccountDto(accountWithSpecCurrency, true));
            } else {
                accountsDto.add(new AccountDto(new Account(currency), false));
            }
        });
        return accountsDto;
    }

    public void updateAccounts(List<Currency> selectedCurrencies) {
        var principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var accounts = accountRepository.findByUser(principal);
        var existedCurrency = accounts.stream().map(Account::getCurrency).toList();
        selectedCurrencies.forEach(currency -> {
            if (!existedCurrency.contains(currency)) {
                accountRepository.save(new Account(currency, principal));
            }
        });
        accounts.forEach(account -> {
            if (!selectedCurrencies.contains(account.getCurrency()) && account.getBalance() == 0.0) {
                accountRepository.delete(account);
            }
        });
    }

    public Boolean withDraw(Currency currency, Double amount) {
        var principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var accountOpt = accountRepository.findByUserAndCurrency(principal, currency);
        if (accountOpt.isPresent()) {
            var account = accountOpt.get();
            if (account.getBalance() >= amount) {
                account.setBalance(account.getBalance() - amount);
                accountRepository.save(account);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public Boolean putSelf(Currency currency, Double amount) {
        var principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return put(currency, amount, principal);
    }

    private boolean put(Currency currency, Double amount, User user) {
        if (user == null) {
            return false;
        }
        var accountOpt = accountRepository.findByUserAndCurrency(user, currency);
        if (accountOpt.isPresent()) {
            var account = accountOpt.get();
            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);
            return true;
        }
        return false;


    }

    public Boolean putAnother(Currency currency, Double amount, String login) {
        var user = userRepository.findByEmail(login);
        return put(currency, amount, user);
    }
}
