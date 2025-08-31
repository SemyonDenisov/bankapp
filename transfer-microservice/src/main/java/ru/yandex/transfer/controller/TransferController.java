package ru.yandex.transfer.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.transfer.model.TransferRequest;
import ru.yandex.transfer.service.TransferService;

@RestController
public class TransferController {

    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transfer(@RequestBody TransferRequest transferRequest) {
        transferService.transfer(transferRequest);
        return ResponseEntity.ok().build();
    }
}
