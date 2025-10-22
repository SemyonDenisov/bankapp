package ru.yandex.account;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.account.dao.AccountRepository;
import ru.yandex.account.dao.UserRepository;
import ru.yandex.account.model.Account;
import ru.yandex.account.model.Currency;
import ru.yandex.account.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfig.class)
public abstract class BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        userRepository.deleteAll();
        accountRepository.deleteAll();
        var user = new User();
        user.setPassword("$2a$10$8yBjbDjy4GEQ/FPJ4RLEJucDGZ9LKdQ4i37zJBDL94UO1Hepcwnue");
        user.setUsername("User One");
        user.setEmail("user@example.com");
        user.setBirthday(LocalDate.now());
        user = userRepository.save(user);
        var user2 = new User();
        user2.setPassword("$2a$10$8yBjbDjy4GEQ/FPJ4RLEJucDGZ9LKdQ4i37zJBDL94UO1Hepcwnue");
        user2.setUsername("User Two");
        user2.setEmail("user2@example.com");
        user2.setBirthday(LocalDate.now());
        userRepository.save(user2);
        accountRepository.save(new Account(Currency.USD, user2));
        accountRepository.save(new Account(Currency.RUB, user));
        var usdAccaount = new Account(Currency.USD, user);
        usdAccaount.setBalance(1500D);
        accountRepository.save(usdAccaount);

        var auth = new UsernamePasswordAuthenticationToken(user, "123", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
