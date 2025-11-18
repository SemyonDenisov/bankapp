package ru.yandex.account.integration.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.account.BaseTest;
import ru.yandex.account.KafkaTestConfig;
import ru.yandex.account.dao.AccountRepository;
import ru.yandex.account.dao.UserRepository;
import ru.yandex.account.model.Currency;
import ru.yandex.account.service.AccountService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"notification.accounts-microservice"})
public class AccountServiceIntegrationTests extends BaseTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;


    @BeforeAll
    static void init(@Autowired EmbeddedKafkaBroker broker) {
        System.setProperty("spring.kafka.bootstrap-servers", broker.getBrokersAsString());
    }
    @Test
    void getAccountsByEmail_shouldReturnAllCurrenciesWithCorrectFlags() {

        var result = accountService.getAccountsByEmail("user@example.com");

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getCurrency() == Currency.USD && a.getExists()));
        assertTrue(result.stream().anyMatch(a -> a.getCurrency() == Currency.RUB && a.getExists()));
        assertTrue(result.stream().anyMatch(a -> a.getCurrency() == Currency.EUR && !a.getExists()));
    }

    @Test
    void updateAccounts_shouldAddAndRemoveAccountsCorrectly() {
        accountService.updateAccounts(List.of(Currency.EUR));


        var accounts = accountRepository.findByUser(userRepository.findByEmail("user@example.com"));
        assertEquals(2, accounts.size());
        assertEquals(Currency.USD, accounts.get(0).getCurrency());
    }

    @Test
    void withDraw_shouldWithdrawSuccessfully() {

        var result = accountService.withDraw(Currency.USD, 500.0);
        assertTrue(result);
    }


    @Test
    void putSelf_shouldAddAmountToExistingAccount() {
        var result = accountService.putSelf(Currency.RUB, 200.0);
        assertTrue(result);
    }

    @Test
    void putAnother_shouldPutToAnotherUserSuccessfully() {
        var result = accountService.putAnother(Currency.USD, 90.0, "user2@example.com");
        assertTrue(result);
    }


}
