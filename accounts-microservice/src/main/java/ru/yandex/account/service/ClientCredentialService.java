package ru.yandex.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
@Profile("!test")
public class ClientCredentialService {

    @Value("${spring.security.oauth2.client.registration.accounts-microservice.client-id}")
    private String clientId;

    private final OAuth2AuthorizedClientManager clientManager;
    private final RestTemplate restTemplate;

    @Autowired
    public ClientCredentialService(OAuth2AuthorizedClientManager clientManager, RestTemplate restTemplate) {
        this.clientManager = clientManager;
        this.restTemplate = restTemplate;
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
