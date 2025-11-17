package ru.yandex.notification.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.notification.TestSecurityConfig;
import ru.yandex.notification.configuration.WebSocketConfig;
import ru.yandex.notification.controller.NotificationsController;
import ru.yandex.notification.security.JwtFilter;
import ru.yandex.notification.security.SecurityConfig;
import ru.yandex.notification.service.NotificationsService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(controllers = NotificationsController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtFilter.class}))
@Import({TestSecurityConfig.class,WebSocketConfig.class,NotificationsService.class})
public class NotificationControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp(){
        var authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("test");
        var securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testSaveAndGetOldNotifications() throws Exception {
        List<String> mockMessages = List.of("message1", "message2");
        String message = "Test message";

        mockMvc.perform(MockMvcRequestBuilders.post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(message))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/old-notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0]").value("Test message"));
    }
}
