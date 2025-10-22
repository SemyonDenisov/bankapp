package ru.yandex.front.ui.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.front.ui.TestSecurityConfig;
import ru.yandex.front.ui.controller.FrontController;
import ru.yandex.front.ui.model.Account;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.model.User;
import ru.yandex.front.ui.service.*;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FrontController.class)
@Import(TestSecurityConfig.class)
class FrontControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @MockitoBean
    private ExchangeService exchangeService;

    @MockitoBean
    private JwtService jwtService;

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
    @WithMockUser("jwt-mock-token")
    void testMainPage_containsFormAndAccounts() throws Exception {
        when(accountService.getAccounts()).thenReturn(List.of(
                new Account( Currency.USD, true,1.0),
                new Account(Currency.RUB, true,1.0)
        ));
        when(accountService.getUsers()).thenReturn(List.of(new User()));
        when(clientCredentialService.getToken()).thenReturn("client-token");

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"));
    }

    @Test
    void testSignUpPageRendersSignupForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

}
