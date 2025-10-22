package ru.yandex.account.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.account.TestConfig;
import ru.yandex.account.controller.UserController;
import ru.yandex.account.model.*;
import ru.yandex.account.service.AccountService;
import ru.yandex.account.service.NotificationService;
import ru.yandex.account.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class UserControllerUnitTests {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockUser.setUsername("Test User");
        mockUser.setPassword("oldPassword");
        mockUser.setBirthday(LocalDate.of(2000, 1, 1));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, List.of())
        );
    }

    @Test
    void updateUser_shouldUpdateUserAndAccounts() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setUsername("New Name");
        updateDto.setBirthday(LocalDate.of(1995, 5, 5));
        updateDto.setSelectedCurrencies(List.of(Currency.USD, Currency.EUR));

        ResponseEntity<Void> response = userController.updateUser(updateDto);

        verify(accountService).updateAccounts(updateDto.getSelectedCurrencies());
        verify(userService).save(mockUser);
        verify(notificationService).sendNotification("edit user success");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New Name", mockUser.getUsername());
        assertEquals(LocalDate.of(1995, 5, 5), mockUser.getBirthday());
    }

    @Test
    void updateUser_shouldNotSaveUserWhenOnlyCurrenciesChanged() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setSelectedCurrencies(List.of(Currency.RUB));

        ResponseEntity<Void> response = userController.updateUser(updateDto);

        verify(accountService).updateAccounts(updateDto.getSelectedCurrencies());
        verify(userService, never()).save(any());
        verify(notificationService).sendNotification("edit user success");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void changePassword_shouldChangePasswordWhenConfirmed() {
        ChangePasswordDto passwordDto = new ChangePasswordDto();
        passwordDto.setPassword("newPass123");
        passwordDto.setConfirmPassword("newPass123");

        ResponseEntity<Void> response = userController.changePassword(passwordDto);

        verify(userService).save(mockUser);
        verify(notificationService).sendNotification("change password success");
        assertEquals("newPass123", mockUser.getPassword());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void changePassword_shouldReturnBadRequestIfPasswordsDoNotMatch() {
        ChangePasswordDto passwordDto = new ChangePasswordDto();
        passwordDto.setPassword("newPass123");
        passwordDto.setConfirmPassword("wrongConfirm");

        ResponseEntity<Void> response = userController.changePassword(passwordDto);

        verify(userService, never()).save(any());
        verify(notificationService, never()).sendNotification(any());
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void getUsers_shouldReturnListOfUserDtos() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setUsername("User One");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setUsername("User Two");

        when(userService.getUsers()).thenReturn(List.of(user1, user2));

        ResponseEntity<List<UserDto>> response = userController.getUsers();

        assertEquals(200, response.getStatusCodeValue());
        List<UserDto> result = response.getBody();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("User Two", result.get(1).getUsername());
    }
}
