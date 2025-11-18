package ru.yandex.account.integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.account.BaseTest;
import ru.yandex.account.dao.UserRepository;
import ru.yandex.account.model.RegistrationForm;
import ru.yandex.account.model.User;
import ru.yandex.account.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTests extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void loadUserByUsername_shouldReturnUser_whenUserExists() {
        var user = new User();
        user.setEmail("user@example.com");
        var result = userService.loadUserByUsername("user@example.com");
        assertEquals("User One", result.getUsername());
    }

    @Test
    void saveNewUser_shouldEncodePasswordAndSaveUser() {
        RegistrationForm form = new RegistrationForm("test@example.com", "Test", "123", "123", LocalDate.now());
        userService.saveNewUser(form);
        assertEquals("123", userService.loadUserByUsername("test@example.com").getUsername());
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        var result = userService.getUsers();
        assertEquals(2, result.size());
    }

}
