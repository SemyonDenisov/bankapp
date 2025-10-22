package ru.yandex.transfer.contracts;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import ru.yandex.transfer.model.CurrencyConversionResponse;
import ru.yandex.transfer.service.ClientCredentialService;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseContractTest {

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private ClientCredentialService clientCredentialService;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        var auth = new UsernamePasswordAuthenticationToken(null, "123", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(clientCredentialService.getToken()).thenReturn("FAKE_SERVICE_TOKEN");
        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setAmount(95.0);

        ResponseEntity<CurrencyConversionResponse> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

        when(restTemplate.exchange(
                contains("exchange-microservice/conversion"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CurrencyConversionResponse.class)
        )).thenReturn(responseEntity);

        ResponseEntity<Void> voidResponse = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                contains("accounts-microservice/accounts/withdraw"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(voidResponse);

        when(restTemplate.exchange(
                contains("accounts-microservice/accounts/put"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(voidResponse);
    }

    @Configuration
    @Profile("test")
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable());
            return http.build();
        }

        @Bean
        public OAuth2AuthorizedClientManager authorizedClientManager() {
            return mock(OAuth2AuthorizedClientManager.class);
        }

        @Bean
        public ClientCredentialService clientCredentialService() {
            return mock(ClientCredentialService.class);
        }
    }

}
