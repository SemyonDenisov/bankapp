package ru.yandex.exchange.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.exchange.generator.security.JwtFilter;
import ru.yandex.exchange.generator.security.SecurityConfig;
import ru.yandex.exchange.generator.service.ClientCredentialService;
import ru.yandex.exchange.generator.service.CurrencyService;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableWebSecurity
@ComponentScan(
        basePackages = "ru.yandex.exchange.generator",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = { SecurityConfig.class, JwtFilter.class,ClientCredentialService.class, CurrencyService.class }
        )
)
@ActiveProfiles("test")
public class TestSecurityConfig {

    @Bean
    @Profile("test")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public ClientCredentialService clientCredentialService(@Qualifier("test") OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager){
        return new ClientCredentialService(oAuth2AuthorizedClientManager);
    }

    @Bean("test")
    public OAuth2AuthorizedClientManager testAuthorizedClientManager() {
        return mock(OAuth2AuthorizedClientManager.class);
    }
}
