package ru.yandex.front.ui.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.front.ui.TestSecurityConfig;
import ru.yandex.front.ui.controller.UserController;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.service.*;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CashService cashService;
    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @BeforeEach
    void setUp() {
        reset(transferService, accountService, cashService);
    }

    @BeforeEach
    void setUpSecurityContext() {
        var authToken = new UsernamePasswordAuthenticationToken(
                "mock-user",
                "mock-password",
                List.of()
        );
        authToken.setDetails("mock-jwt-token");
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    void testChangePassword_Success() throws Exception {
        mockMvc.perform(post("/user/editPassword")
                        .param("password", "1234")
                        .param("confirm_password", "1234")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(accountService).changePassword("1234", "1234");
    }

    @Test
    void testEditAccount_WithValidData() throws Exception {
        mockMvc.perform(post("/user/editUserAccounts")
                        .param("name", "Иван Иванов")
                        .param("birthdate", "1990-01-01")
                        .param("account", "RUB")
                        .param("account", "USD")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(accountService).editAccount(eq("Иван Иванов"), eq(LocalDate.of(1990, 1, 1)),
                ArgumentMatchers.<List<Currency>>any());
    }

    @Test
    void testEditAccount_WithoutChanges() throws Exception {
        mockMvc.perform(post("/user/editUserAccounts").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(accountService, never()).editAccount(any(), any(), any());
    }

    @Test
    void testHandleCashAction_PUT() throws Exception {
        mockMvc.perform(post("/user/cash")
                        .param("currency", "RUB")
                        .param("value", "1000")
                        .param("action", "PUT")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(cashService).put(Currency.RUB, 1000.0);
    }

    @Test
    void testHandleCashAction_GET() throws Exception {
        mockMvc.perform(post("/user/cash")
                        .param("currency", "USD")
                        .param("value", "500")
                        .param("action", "GET")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(cashService).withdraw(Currency.USD, 500.0);
    }

    @Test
    void testHandleCashAction_UnknownAction() throws Exception {
        mockMvc.perform(post("/user/cash")
                        .param("currency", "EUR")
                        .param("value", "100")
                        .param("action", "INVALID")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verifyNoInteractions(cashService);
    }

    @Test
    void testTransfer_SelfTransfer() throws Exception {
        mockMvc.perform(post("/user/transfer")
                        .param("from_currency", "RUB")
                        .param("to_currency", "USD")
                        .param("value", "2500")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(transferService).selfTransfer(Currency.RUB, Currency.USD, 2500.0);
    }

    @Test
    void testTransfer_ToAnotherUser() throws Exception {
        mockMvc.perform(post("/user/transfer")
                        .param("from_currency", "RUB")
                        .param("to_currency", "USD")
                        .param("value", "1000")
                        .param("to_login", "user123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(transferService).transferToAnother(Currency.RUB, Currency.USD, 1000.0, "user123");
    }
}
