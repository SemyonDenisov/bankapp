package ru.yandex.account;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.account.service.ClientCredentialService;


import static org.mockito.Mockito.mock;

@TestConfiguration
@ActiveProfiles("test")
@Profile("test")
public class TestConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }


    @Bean
    @Profile("test")
    public OAuth2AuthorizedClientManager authorizedClientManager() {
        return mock(OAuth2AuthorizedClientManager.class);
    }



    @Bean
    @Profile("test")
    public ClientCredentialService clientCredentialService(OAuth2AuthorizedClientManager authorizedClientManager) {
        return new ClientCredentialService(authorizedClientManager, null) {
        };
    }


}
