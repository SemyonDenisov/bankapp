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
import ru.yandex.account.controller.AccountController;
import ru.yandex.account.model.*;
import ru.yandex.account.service.AccountService;
import ru.yandex.account.service.NotificationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class AccountsControllerUnitTests {

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private AccountController accountController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setEmail("user@example.com");

        var auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getAccounts_shouldReturnAccountList() {
        List<AccountDto> mockAccounts = List.of(new AccountDto(new Account(Currency.USD), true));
        when(accountService.getAccountsByEmail("user@example.com")).thenReturn(mockAccounts);

        ResponseEntity<List<AccountDto>> response = accountController.getAccounts();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAccounts, response.getBody());
    }

    @Test
    void putCash_shouldPutToAnotherUser() {
        when(accountService.putAnother(Currency.EUR, 100.0, "other@example.com")).thenReturn(true);

        ResponseEntity<Void> response = accountController.putCash(Currency.EUR, 100.0, "other@example.com");

        assertEquals(200, response.getStatusCodeValue());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    void putCash_shouldPutToSelfAndSendSuccessNotification() {
        when(accountService.putAnother(any(), anyDouble(), any())).thenReturn(false);
        when(accountService.putSelf(Currency.USD, 50.0)).thenReturn(true);

        ResponseEntity<Void> response = accountController.putCash(Currency.USD, 50.0, null);

        assertEquals(200, response.getStatusCodeValue());
        verify(notificationService).sendNotification("money put success");
    }


    @Test
    void withdrawCash_shouldWithdrawSuccessfully() {
        when(accountService.withDraw(Currency.EUR, 20.0)).thenReturn(true);

        ResponseEntity<Void> response = accountController.withdrawCash(Currency.EUR, 20.0);

        assertEquals(200, response.getStatusCodeValue());
        verify(notificationService).sendNotification("money withdraw success");
    }
}
