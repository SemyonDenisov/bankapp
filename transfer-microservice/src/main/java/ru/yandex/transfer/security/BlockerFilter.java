package ru.yandex.transfer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.yandex.transfer.service.ClientCredentialService;

import java.io.IOException;

@Component
public class BlockerFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;

    private final ClientCredentialService clientCredentialService;
    private final ObjectMapper mapper = new ObjectMapper();
    MeterRegistry meterRegistry;

    public BlockerFilter(RestTemplate restTemplate, ClientCredentialService clientCredentialService) {
        this.restTemplate = restTemplate;
        this.clientCredentialService = clientCredentialService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull
                                    HttpServletResponse response,
                                    @NonNull
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/actuator/prometheus")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            var email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            var token = clientCredentialService.getToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            var decision = restTemplate.exchange("http://blocker-microservice/block", HttpMethod.GET, entity, Boolean.class);
            if (Boolean.TRUE.equals(decision.getBody())) {
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
                wrappedRequest.getInputStream().readAllBytes();
                String body = new String(wrappedRequest.getContentAsByteArray(),
                        request.getCharacterEncoding());

                ru.yandex.front.ui.model.TransferRequest transferRequest = mapper.readValue(body, ru.yandex.front.ui.model.TransferRequest.class);

                meterRegistry.counter("block_operation", "from", email,
                        "to", transferRequest.getLogin(),
                        "sender_bill", transferRequest.getFromCurrency().toString(),
                        "consumer_bill", transferRequest.getToCurrency().toString());

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

