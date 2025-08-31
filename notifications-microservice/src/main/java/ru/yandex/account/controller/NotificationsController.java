package ru.yandex.account.controller;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.account.model.Message;

@RestController
public class NotificationsController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/send-alert")
    public void sendAlert(@RequestBody Message message) {
        messagingTemplate.convertAndSend("/topic/alerts", message.getMessage());
    }
}
