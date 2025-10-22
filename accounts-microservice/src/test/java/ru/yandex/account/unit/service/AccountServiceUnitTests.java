package ru.yandex.account.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.account.TestConfig;
import ru.yandex.account.dao.AccountRepository;
import ru.yandex.account.dao.UserRepository;
import ru.yandex.account.model.Account;
import ru.yandex.account.model.Currency;
import ru.yandex.account.model.User;
import ru.yandex.account.service.AccountService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class AccountServiceUnitTests {

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void resetMocks() {
        reset(userRepository);
        reset(accountRepository);
        var user = new User();
        user.setPassword("$2a$10$8yBjbDjy4GEQ/FPJ4RLEJucDGZ9LKdQ4i37zJBDL94UO1Hepcwnue");
        user.setUsername("User One");
        user.setEmail("user@example.com");
        user.setBirthday(LocalDate.now());
        var auth = new UsernamePasswordAuthenticationToken(user, "123", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    void getAccountsByEmail_shouldReturnAllCurrenciesWithCorrectFlags() {
        var user = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        var account = new Account(Currency.USD, user);
        account.setBalance(1500.0);
        when(accountRepository.findByUser(user)).thenReturn(List.of(account));

        var result = accountService.getAccountsByEmail("test@example.com");

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getCurrency() == Currency.USD && a.getExists()));
        assertTrue(result.stream().anyMatch(a -> a.getCurrency() == Currency.RUB && !a.getExists()));
        assertTrue(result.stream().anyMatch(a -> a.getCurrency() == Currency.EUR && !a.getExists()));
    }

    @Test
    void updateAccounts_shouldAddAndRemoveAccountsCorrectly() {
        var user = new User();
        var existingAccount = new Account(Currency.USD, user);
        existingAccount.setBalance(0.0);

        when(accountRepository.findByUser(any())).thenReturn(List.of(existingAccount));

        accountService.updateAccounts(List.of(Currency.EUR));

        verify(accountRepository).save(argThat(a -> a.getCurrency() == Currency.EUR));
        verify(accountRepository).delete(any());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void withDraw_shouldWithdrawSuccessfully() {
        var user = new User();
        var auth = new UsernamePasswordAuthenticationToken(user, "123", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        var account = new Account(Currency.USD, user);
        account.setBalance(1000.0);

        when(accountRepository.findByUserAndCurrency(any(), eq(Currency.USD))).thenReturn(Optional.of(account));

        var result = accountService.withDraw(Currency.USD, 500.0);

        assertTrue(result);
        assertEquals(500.0, account.getBalance());
        verify(accountRepository).save(account);
    }


    @Test
    void putSelf_shouldAddAmountToExistingAccount() {
        var user = new User();
        var auth = new UsernamePasswordAuthenticationToken(user, "123", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        var account = new Account(Currency.EUR, user);
        account.setBalance(100.0);

        when(accountRepository.findByUserAndCurrency(user, Currency.EUR)).thenReturn(Optional.of(account));

        var result = accountService.putSelf(Currency.EUR, 200.0);

        assertTrue(result);
        assertEquals(300.0, account.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void putAnother_shouldPutToAnotherUserSuccessfully() {
        var user = new User();
        var auth = new UsernamePasswordAuthenticationToken(user, "123", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        var anotherUser = new User();
        var account = new Account(Currency.RUB, anotherUser);
        account.setBalance(10.0);

        when(userRepository.findByEmail("other@example.com")).thenReturn(anotherUser);
        when(accountRepository.findByUserAndCurrency(anotherUser, Currency.RUB)).thenReturn(Optional.of(account));

        var result = accountService.putAnother(Currency.RUB, 90.0, "other@example.com");

        assertTrue(result);
        assertEquals(100.0, account.getBalance());
        verify(accountRepository).save(account);
    }


}
