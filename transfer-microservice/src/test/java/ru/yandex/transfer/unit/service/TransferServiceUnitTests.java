package ru.yandex.transfer.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.model.TransferRequest;
import ru.yandex.transfer.TestSecurityConfig;
import ru.yandex.transfer.model.Currency;
import ru.yandex.transfer.model.CurrencyConversionResponse;
import ru.yandex.transfer.service.ClientCredentialService;
import ru.yandex.transfer.service.TransferService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
public class TransferServiceUnitTests {

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @Autowired
    private TransferService transferService;

    @BeforeEach
    public void setUp() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(
                new UsernamePasswordAuthenticationToken("user", "user-token")
        );
        SecurityContextHolder.setContext(context);
    }

    @Test
    public void testTransfer_ValidRequest_ReturnsTrue() {
        TransferRequest request = new TransferRequest();
        request.setFromCurrency(Currency.EUR);
        request.setToCurrency(Currency.USD);
        request.setAmount(100.0);
        request.setLogin("receiver");

        when(clientCredentialService.getToken()).thenReturn("service-token");

        CurrencyConversionResponse conversionResponse = new CurrencyConversionResponse();
        conversionResponse.setAmount(95.0);

        ResponseEntity<CurrencyConversionResponse> conversionResponseEntity =
                new ResponseEntity<>(conversionResponse, HttpStatus.OK);
        ResponseEntity<Void> voidResponse = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                contains("/conversion"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CurrencyConversionResponse.class))
        ).thenReturn(conversionResponseEntity);

        when(restTemplate.exchange(
                contains("/accounts/withdraw"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class))
        ).thenReturn(voidResponse);

        when(restTemplate.exchange(
                contains("/accounts/put"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class))
        ).thenReturn(voidResponse);

        boolean result = transferService.transfer(request);


        assertTrue(result);

        verify(restTemplate, times(1)).exchange(
                contains("/conversion"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CurrencyConversionResponse.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("withdraw"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("put"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }

    @Test
    public void testGetRequest_ReturnsExpectedValue() {
        String url = "http://example.com";
        String token = "token";

        ResponseEntity<String> response = new ResponseEntity<>("success", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(response);

        String result = transferService.getRequest(url, String.class, token);
        assertEquals("success", result);
    }

    @Test
    public void testPostRequest_ReturnsExpectedValue() {
        String url = "http://example.com";
        String token = "token";

        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(response);

        String result = transferService.postRequest(url, String.class, token);
        assertEquals("ok", result);
    }
}
