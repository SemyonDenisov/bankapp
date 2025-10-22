package ru.yandex.exchange.generator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("!test")
public class ClientCredentialService {

    @Value("${spring.security.oauth2.client.registration.exchange-generator-microservice.client-id}")
    private String clientId;

    private final OAuth2AuthorizedClientManager clientManager;

    public ClientCredentialService(@Qualifier("authorizedClientManager") OAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public String getToken() {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientId)
                .principal("client")
                .build();

        OAuth2AuthorizedClient client = clientManager.authorize(request);

        if (client == null) {
            throw new IllegalStateException("Failed to authorize client");
        }
        return client.getAccessToken().getTokenValue();
    }
}
