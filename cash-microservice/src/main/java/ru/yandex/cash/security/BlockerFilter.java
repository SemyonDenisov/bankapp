package ru.yandex.cash.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.yandex.cash.service.ClientCredentialService;
import ru.yandex.cash.service.NotificationService;

import java.io.IOException;

@Component
public class BlockerFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;

    private final ClientCredentialService clientCredentialService;
    private final NotificationService notificationService;

    public BlockerFilter(RestTemplate restTemplate,ClientCredentialService clientCredentialService,
                         NotificationService notificationService) {
        this.restTemplate = restTemplate;
        this.clientCredentialService = clientCredentialService;
        this.notificationService = notificationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull
                                    HttpServletResponse response,
                                    @NonNull
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (request.getRequestURI().equals("/actuator/prometheus")) {
                filterChain.doFilter(request, response);
                return;
            }
            var token = clientCredentialService.getToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            var decision = restTemplate.exchange("http://blocker-microservice/block", HttpMethod.GET,entity,Boolean.class);
            if (Boolean.TRUE.equals(decision.getBody())) {
                notificationService.sendNotification("Suspicious operation");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Suspicious operation");
                return;
            }
        } catch (RestClientException e) {
            notificationService.sendNotification("Suspicious operation");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Suspicious operation");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

