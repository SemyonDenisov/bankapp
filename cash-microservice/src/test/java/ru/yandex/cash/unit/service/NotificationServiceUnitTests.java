package ru.yandex.cash.unit.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import ru.yandex.cash.service.NotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = NotificationService.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class NotificationServiceUnitTests {

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn("fake-token");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void sendNotification_success() {
        when(restTemplate.postForObject(
                eq("http://notifications-microservice/notify"),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn("ok");

        notificationService.sendNotification("test message");

        verify(restTemplate, times(1))
                .postForObject(eq("http://notifications-microservice/notify"), any(HttpEntity.class), eq(String.class));
    }
}

