package ru.yandex.front.ui.contracts;


import org.junit.jupiter.api.Test;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.service.AccountService;
import ru.yandex.front.ui.service.ClientCredentialService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureStubRunner(
        ids = "ru.yandex:transfer-microservice:+:stubs:8083",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("test")
public class TransferTests extends BaseContractTest{

    private final RestTemplate restTemplate = new RestTemplate();

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private ClientCredentialService clientCredentialService;


    @Test
    void shouldTransferSuccessfullyAccordingToContract() {
        String url = "http://localhost:8083/transfer";

        Map<String, Object> requestBody = Map.of(
                "from_currency", "EUR",
                "to_currency", "USD",
                "amount", 100.0,
                "login", "receiver"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
