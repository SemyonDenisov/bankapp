package ru.yandex.exchange.generator.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import ru.yandex.exchange.generator.TestSecurityConfig;
import ru.yandex.exchange.generator.model.Currency;
import ru.yandex.exchange.generator.model.CurrencyQuotation;
import ru.yandex.exchange.generator.service.ClientCredentialService;
import ru.yandex.exchange.generator.service.CurrencyService;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class CurrencyServiceUnitTests {

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @Autowired
    private CurrencyService currencyService;


    @Test
    void generateExchangeRate_shouldReturnRateInCorrectRangeForUSD() {
        double rate = currencyService.generateExchangeRate(Currency.USD);
        assertTrue(rate >= 90 && rate <= 110);
    }

    @Test
    void generateExchangeRate_shouldReturnRateInCorrectRangeForEUR() {
        double rate = currencyService.generateExchangeRate(Currency.EUR);
        assertTrue(rate >= 90 && rate <= 110);
    }

    @Test
    void generateExchangeRate_shouldReturnOneForRUB() {
        double rate = currencyService.generateExchangeRate(Currency.RUB);
        assertEquals(1.0, rate);
    }

    @Test
    void generateCurrency_shouldCallExchangeMicroservice_whenNotBlocked() {
        when(clientCredentialService.getToken()).thenReturn("mock-token");

        ResponseEntity<Boolean> blockResponse = new ResponseEntity<>(false, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("http://blocker-microservice/block"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(blockResponse);

        ResponseEntity<Void> postResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.postForEntity(
                eq("http://exchange-microservice/update-quotations"),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(postResponse);

        currencyService.generateCurrency();

        verify(restTemplate).postForEntity(
                eq("http://exchange-microservice/update-quotations"),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }

    @Test
    void generateCurrency_shouldNotCallExchangeMicroservice_whenBlocked() {
        when(clientCredentialService.getToken()).thenReturn("mock-token");

        ResponseEntity<Boolean> blockResponse = new ResponseEntity<>(true, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("http://blocker-microservice/block"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(blockResponse);

        currencyService.generateCurrency();

        verify(restTemplate, never()).postForEntity(
                eq("http://exchange-microservice/update-quotations"),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }
}
