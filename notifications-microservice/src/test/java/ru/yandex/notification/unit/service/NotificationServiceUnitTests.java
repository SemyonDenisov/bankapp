package ru.yandex.notification.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.account.service.NotificationsService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class NotificationServiceUnitTests {
    @Autowired
    private NotificationsService notificationsService;

    @BeforeEach
    void setUp() {
        notificationsService.saveOldMessagesByEmail("1","1");
        notificationsService.saveOldMessagesByEmail("1","2");
    }

    @Test
    public void test_getOldMessagesByEmail(){
        assert notificationsService.getOldMessagesByEmail("1").size()==2;
        assert notificationsService.getOldMessagesByEmail("1").isEmpty();
    }


    @Test
    void test_setOldMessagesByEmail(){
        notificationsService.saveOldMessagesByEmail("1","1");
        assert notificationsService.getOldMessagesByEmail("1").size()==3;
    }

}
