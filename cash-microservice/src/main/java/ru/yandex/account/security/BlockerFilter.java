package ru.yandex.account.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BlockerFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;

    public BlockerFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull
                                    HttpServletResponse response,
                                    @NonNull
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Boolean decision = restTemplate.getForObject("http://blocker-microservice/block",Boolean.class);
            if (Boolean.TRUE.equals(decision)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Suspicious operation");
                return;
            }
        } catch (RestClientException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Suspicious operation");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

