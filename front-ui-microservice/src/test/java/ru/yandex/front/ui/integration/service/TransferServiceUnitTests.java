package ru.yandex.front.ui.integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.TestSecurityConfig;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.model.TransferRequest;
import ru.yandex.front.ui.service.ClientCredentialService;
import ru.yandex.front.ui.service.TransferService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class TransferServiceUnitTests {

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @Autowired
    private TransferService transferService;

    @BeforeEach
    void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getDetails()).thenReturn("mock-token");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void testSelfTransfer_success() {
        Currency from = Currency.USD;
        Currency to = Currency.RUB;
        double amount = 100.0;
        String url = "http://transfer-microservice/transfer";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        boolean result = transferService.selfTransfer(from, to, amount);

        assertTrue(result);
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class));
    }

    @Test
    void testTransferToAnother_success() {
        Currency from = Currency.EUR;
        Currency to = Currency.USD;
        double amount = 50.0;
        String login = "user123";
        String url = "http://transfer-microservice/transfer";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        boolean result = transferService.transferToAnother(from, to, amount, login);

        assertTrue(result);
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class));
    }

    @Test
    void testPostRequest_tokenIsSet() {
        TransferRequest request = new TransferRequest(Currency.RUB, Currency.USD, 10.0, "");
        String url = "http://transfer-microservice/transfer";

        ArgumentCaptor<HttpEntity<TransferRequest>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), entityCaptor.capture(), eq(Boolean.class)))
                .thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        transferService.postRequest(request, url, HttpMethod.POST);

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("Bearer mock-token", headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void testPostRequest_returnsFalseIfNull() {
        TransferRequest request = new TransferRequest(Currency.EUR, Currency.RUB, 20.0, "");
        String url = "http://transfer-microservice/transfer";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        boolean result = transferService.postRequest(request, url, HttpMethod.POST);
        assertFalse(result);
    }

    @Test
    void testPostRequest_returnsFalseIfFalseReturned() {
        TransferRequest request = new TransferRequest(Currency.EUR, Currency.RUB, 20.0, "");
        String url = "http://transfer-microservice/transfer";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class)))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        boolean result = transferService.postRequest(request, url, HttpMethod.POST);
        assertFalse(result);
    }
}
