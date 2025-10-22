package ru.yandex.account.unit.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.account.TestSecurityConfig;
import ru.yandex.account.controller.NotificationsController;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.yandex.account.security.JwtFilter;
import ru.yandex.account.security.SecurityConfig;
import ru.yandex.account.service.NotificationsService;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = NotificationsController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtFilter.class}))
@Import(TestSecurityConfig.class)
public class NotificationsControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @MockitoBean
    private NotificationsService notificationsService;


    @BeforeEach
    void setUp() throws ServletException, IOException {
        var authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("test");

        var securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testSendAlert() throws Exception {
        String message = "Test message";

        mockMvc.perform(MockMvcRequestBuilders.post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(message))
                .andExpect(status().isOk());

        Mockito.verify(messagingTemplate).convertAndSend("/topic/alerts", "message=" + message);

        Mockito.verify(notificationsService)
                .saveOldMessagesByEmail("anonymousUser", message);
    }

    @Test
    void testGetOldNotifications() throws Exception {
        List<String> mockMessages = List.of("message1", "message2");

        Mockito.when(notificationsService.getOldMessagesByEmail(any()))
                .thenReturn(mockMessages);

        mockMvc.perform(MockMvcRequestBuilders.get("/old-notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0]").value("message1"))
                .andExpect(jsonPath("$[1]").value("message2"));

        Mockito.verify(notificationsService).getOldMessagesByEmail("anonymousUser");
    }
}
