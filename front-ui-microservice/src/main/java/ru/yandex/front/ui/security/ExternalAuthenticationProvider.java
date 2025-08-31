package ru.yandex.front.ui.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.model.LoginForm;
import ru.yandex.front.ui.model.Token;

import java.util.List;

@Component
public class ExternalAuthenticationProvider implements AuthenticationProvider {

    private final RestTemplate restTemplate;

    public ExternalAuthenticationProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // DTO для запроса
        var loginForm = new LoginForm(username, password);

        try {
            // Отправляем запрос во внешний сервис аутентификации
            ResponseEntity<Token> response = restTemplate.postForEntity(
                    "http://accounts-microservice/login",
                    loginForm,
                    Token.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String token = response.getBody().getToken();

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                // Можно сохранить токен в details, чтобы потом использовать
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, password, authorities);
                auth.setDetails(token);

                return auth;
            } else {
                throw new BadCredentialsException("Invalid credentials");
            }
        } catch (RestClientException e) {
            throw new AuthenticationServiceException("Authentication service unavailable", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // Вспомогательный класс для десериализации ответа
    public static class AuthResponse {
        private String token;

        // геттеры и сеттеры
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}

