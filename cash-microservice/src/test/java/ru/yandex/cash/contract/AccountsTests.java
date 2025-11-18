package ru.yandex.cash.contract;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.Map;


@SpringBootTest
@AutoConfigureStubRunner(
        ids = "ru.yandex:accounts-microservice:+:stubs:8074",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"notification.accounts-microservice"})
public class AccountsTests {

    RestTemplate restTemplate = new RestTemplate();

    @BeforeAll
    static void init(@Autowired EmbeddedKafkaBroker broker) {
        System.setProperty("spring.kafka.bootstrap-servers", broker.getBrokersAsString());
    }

    @Test
    void shouldPutCashSuccessfully() {



        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8074/accounts/put")
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
        String url = "http://localhost:8074/accounts";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(url,
                org.springframework.http.HttpMethod.GET,
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
        String url = "http://localhost:8074/accounts/put?currency=USD&amount=500.0&login=user@example.com";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 123");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturn200WhenWithdrawCashSuccess() {
        String url = "http://localhost:8074/accounts/withdraw?currency=USD&amount=200.0";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer some-valid-token");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

