package ru.yandex.account.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.account.BaseTest;
import ru.yandex.account.TestConfig;
import ru.yandex.account.dao.UserRepository;
import ru.yandex.account.model.RegistrationForm;
import ru.yandex.account.model.User;
import ru.yandex.account.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class UserServiceUnitTests{

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void reset() {
        Mockito.reset(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUser_whenUserExists() {
        var user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        var result = userService.loadUserByUsername("test@example.com");

        assertEquals(user, result);
    }

    @Test
    void saveNewUser_shouldEncodePasswordAndSaveUser() {
        RegistrationForm form = new RegistrationForm("test@example.com", "Test", "123", "123", LocalDate.now());
        when(passwordEncoder.encode("123")).thenReturn("encoded-password");

        userService.saveNewUser(form);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void save_shouldEncodePasswordAndSaveUser() {
        var user = new User();
        user.setPassword("plain");
        when(passwordEncoder.encode("plain")).thenReturn("hashed");

        userService.save(user);

        assertEquals("hashed", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        var result = userService.getUsers();

        assertEquals(2, result.size());
        assertSame(users, result);
    }




}
