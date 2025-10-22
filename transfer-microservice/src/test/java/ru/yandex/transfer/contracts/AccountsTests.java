package ru.yandex.transfer.contracts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.transfer.service.ClientCredentialService;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@AutoConfigureStubRunner(
        ids = "ru.yandex:accounts-microservice:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("test")
public class AccountsTests extends BaseContractProviderTest {

    RestTemplate restTemplate = new RestTemplate();


    @Test
    void shouldPutCashSuccessfully() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/accounts/put")
                .queryParam("currency", "RUB")
                .queryParam("amount", "100.0")
                .queryParam("login", "user@example.com")
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.POST, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturnAccountsListAsPerContract() {
        String url = "http://localhost:8080/accounts";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(url,
                HttpMethod.GET,
                entity,
                List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Map<String, Object>> accounts = response.getBody();

        assertNotNull(accounts);
        assertEquals(3, accounts.size());

        Map<String, Object> firstAccount = accounts.get(0);
        assertEquals(true, firstAccount.get("exists"));
        assertEquals("USD", firstAccount.get("currency"));
        assertEquals(1500.0, ((Number) firstAccount.get("balance")).doubleValue());
    }

    @Test
    public void shouldReturn200WhenPutCashAnotherSuccess() {
        String url = "http://localhost:8080/accounts/put?currency=USD&amount=500.0&login=user@example.com";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturn200WhenWithdrawCashSuccess() {
        String url = "http://localhost:8080/accounts/withdraw?currency=USD&amount=200.0";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer some-valid-token");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

