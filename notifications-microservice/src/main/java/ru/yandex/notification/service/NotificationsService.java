package ru.yandex.notification.service;


import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.notification.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationsService {

    Map<String, List<String>> oldMessages = new ConcurrentHashMap<>();

    LogService log;

    public NotificationsService(LogService logService) {
        this.log = logService;
    }

    public List<String> getOldMessagesByEmail(String email) {
        var oldPersonalMessages = oldMessages.containsKey(email) ? oldMessages.get(email) : new ArrayList<String>();
        oldMessages.remove(email);
        return oldPersonalMessages;
    }

    public void saveOldMessagesByEmail(String email, String message) {
        oldMessages.putIfAbsent(email, new ArrayList<>());
        oldMessages.get(email).add(message);
    }

    @KafkaListener(topicPattern = "notification.*", groupId = "notification-service")
    public void listen(Message message) {
        log.info("получены сообщения");
        saveOldMessagesByEmail(message.getEmail(), message.getMessage());
    }


}
