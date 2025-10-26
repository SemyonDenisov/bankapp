package ru.yandex.front.ui.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.TestSecurityConfig;
import ru.yandex.front.ui.model.Currency;
import ru.yandex.front.ui.model.CurrencyQuotation;
import ru.yandex.front.ui.service.ClientCredentialService;
import ru.yandex.front.ui.service.ExchangeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@DirtiesContext
class ExchangeServiceUnitTests {

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private ExchangeService exchangeService;

    private final String token = "mocked-token";

    @BeforeEach
    void setUp() {
        when(clientCredentialService.getToken()).thenReturn(token);
        reset(restTemplate);
    }

    @Test
    void testGetRates_returnsRates() {
        CurrencyQuotation q1 = new CurrencyQuotation(Currency.USD, 95.0);
        CurrencyQuotation q2 = new CurrencyQuotation(Currency.EUR, 101.0);
        List<CurrencyQuotation> mockRates = List.of(q1, q2);

        ResponseEntity<List<CurrencyQuotation>> response = new ResponseEntity<>(mockRates, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://api-gateway/exchange/rates"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<CurrencyQuotation>>>any()
        )).thenReturn(response);

        List<CurrencyQuotation> result = exchangeService.getRates();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Currency.USD, result.get(0).getCurrency());
        verify(clientCredentialService).getToken();
        verify(restTemplate).exchange(
                contains("/rates"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void testGetRates_returnsEmptyList() {
        ResponseEntity<List<CurrencyQuotation>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);

        when(restTemplate.exchange(
                contains("/rates"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<CurrencyQuotation>>>any()
        )).thenReturn(response);

        List<CurrencyQuotation> result = exchangeService.getRates();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRates_usesBearerToken() {
        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);

        ResponseEntity<List<CurrencyQuotation>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);
        when(restTemplate.exchange(
                contains("/rates"),
                eq(HttpMethod.GET),
                captor.capture(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        exchangeService.getRates();

        HttpHeaders headers = captor.getValue().getHeaders();
        assertTrue(headers.containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals("Bearer " + token, headers.getFirst(HttpHeaders.AUTHORIZATION));
    }
}
