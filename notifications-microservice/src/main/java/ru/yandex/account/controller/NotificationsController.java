package ru.yandex.account.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.account.service.NotificationsService;

import java.util.List;

@RestController
public class NotificationsController {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationsService notificationsService;


    public NotificationsController(SimpMessagingTemplate messagingTemplate, NotificationsService notificationsService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationsService = notificationsService;
    }

    @PostMapping("/notify")
    public void sendAlert(@RequestBody String message) {
        messagingTemplate.convertAndSend("/topic/alerts", "message=" + message);
        notificationsService.saveOldMessagesByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), message);
    }

    @GetMapping("/old-notifications")
    public ResponseEntity<List<String>> getOldNotifications() {
        var a = notificationsService.getOldMessagesByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return ResponseEntity.ok(a);
    }
}
