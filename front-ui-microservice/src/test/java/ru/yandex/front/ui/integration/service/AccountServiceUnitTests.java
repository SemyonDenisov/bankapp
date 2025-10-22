package ru.yandex.front.ui.integration.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.TestSecurityConfig;
import ru.yandex.front.ui.model.RegistrationForm;
import ru.yandex.front.ui.service.AccountService;
import ru.yandex.front.ui.service.ClientCredentialService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
public class AccountServiceUnitTests {

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        var authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getDetails()).thenReturn("test-token");
        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }



    @Test
    void testRegistration() {
        RegistrationForm form = new RegistrationForm();
        String token = "test-token";

        when(clientCredentialService.getToken()).thenReturn(token);
        when(restTemplate.exchange(
                eq("http://accounts-microservice/registration"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        Boolean result = accountService.registration(form);

        assertTrue(result);
        verify(restTemplate).exchange(
                eq("http://accounts-microservice/registration"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }


}
