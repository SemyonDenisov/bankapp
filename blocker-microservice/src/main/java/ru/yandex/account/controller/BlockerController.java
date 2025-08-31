package ru.yandex.account.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.account.service.BlockerService;

@RestController
public class BlockerController {

    BlockerService blockerService;

    public BlockerController(BlockerService blockerService) {
        this.blockerService = blockerService;
    }

    @GetMapping("/block")
    public ResponseEntity<Boolean> block() {
        return new ResponseEntity<>(blockerService.block(), HttpStatus.OK);
    }

}
