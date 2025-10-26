package ru.yandex.front.ui.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.TestSecurityConfig;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.service.CashService;
import ru.yandex.front.ui.service.ClientCredentialService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
public class CashServiceUnitTests {

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private CashService cashService;

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @BeforeEach
    void setUp() {
        reset(restTemplate);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getDetails()).thenReturn("mock-token");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void testWithdraw_Success() {
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                contains("/withdraw"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(response);

        boolean result = cashService.withdraw(Currency.USD, 100.0);

        assertTrue(result);
    }

    @Test
    void testPut_Success() {
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                contains("/put"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(response);

        boolean result = cashService.put(Currency.EUR, 50.0);

        assertTrue(result);
    }


    @Test
    void testChangeBalance_SetsBearerAuth() {
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                contains("/put"),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(Void.class)
        )).thenReturn(response);

        cashService.changeBalance("http://api-gateway/cash/put", Currency.USD, 123.45);

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertTrue(headers.containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals("Bearer mock-token", headers.getFirst(HttpHeaders.AUTHORIZATION));
    }
}
