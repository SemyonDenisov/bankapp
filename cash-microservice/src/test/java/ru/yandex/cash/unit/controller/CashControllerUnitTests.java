package ru.yandex.cash.unit.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.cash.controller.CashController;
import ru.yandex.cash.model.Currency;
import ru.yandex.cash.service.CashService;
import ru.yandex.cash.service.NotificationService;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CashController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class CashControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CashService cashService;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void test_withdraw_success() throws Exception {
        Mockito.when(cashService.withdraw(eq(Currency.USD), eq(100.0))).thenReturn(true);

        mockMvc.perform(post("/withdraw")
                        .param("amount", "100")
                        .param("currency", "USD")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        Mockito.verify(cashService).withdraw(Currency.USD, 100.0);
        Mockito.verify(notificationService).sendNotification("cash service: withdraw success");
    }



    @Test
    void test_put_success() throws Exception {
        Mockito.when(cashService.put(eq(Currency.RUB), eq(500.0))).thenReturn(true);

        mockMvc.perform(post("/put")
                        .param("amount", "500")
                        .param("currency", "RUB")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        Mockito.verify(cashService).put(Currency.RUB, 500.0);
        Mockito.verify(notificationService).sendNotification("cash service: put success");
    }

}
