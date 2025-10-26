package ru.yandex.transfer.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.transfer.service.NotificationService;
import ru.yandex.transfer.service.TransferService;

@Slf4j
@RestController
public class TransferController {

    TransferService transferService;

    NotificationService notificationService;

    public TransferController(TransferService transferService,NotificationService notificationService) {
        this.transferService = transferService;
        this.notificationService = notificationService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transfer(@RequestBody ru.yandex.front.ui.model.TransferRequest transferRequest) {
        log.info("\n{}\n", transferRequest);
        if (transferService.transfer(transferRequest)) {
            notificationService.sendNotification("transfer-service: transfer success");
            return ResponseEntity.ok().build();
        }
        log.info("\nerrrrrrrrrrrror\n");
        notificationService.sendNotification("transfer-service: transfer error");
        return ResponseEntity.badRequest().build();
    }
}
