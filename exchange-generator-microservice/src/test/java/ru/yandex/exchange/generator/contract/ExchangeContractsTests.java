package ru.yandex.exchange.generator.contract;


import org.junit.jupiter.api.Test;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureStubRunner(
        ids = "ru.yandex:exchange-microservice:+:stubs:8074",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("test")
public class ExchangeContractsTests extends BaseContractProviderTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void shouldReturnCurrencyRates() {
        String url = "http://localhost:8074/rates";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        Map<String, Object> rate = (Map<String, Object>) response.getBody().get(0);
        assertThat(rate).containsKeys("currency", "rate");
    }

    @Test
    void shouldAcceptUpdateQuotations() {
        String url = "http://localhost:8074/update-quotations";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
                [
                    {"currency": "USD", "rate": 75.34},
                    {"currency": "EUR", "rate": 89.12}
                ]
                """;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldConvertCurrencySuccessfully() {
        String url = "http://localhost:8074/conversion?from=USD&to=EUR&amount=100.0";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());

        assertEquals("USD", response.getBody().get("from"));
        assertEquals("EUR", response.getBody().get("to"));
        assertTrue(((Number) response.getBody().get("amount")).doubleValue() > 0.0);
    }
}

