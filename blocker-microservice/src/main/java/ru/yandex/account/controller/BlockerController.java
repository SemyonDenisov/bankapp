package ru.yandex.account.controller;


import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.account.service.BlockerService;

@RestController
public class BlockerController {

    BlockerService blockerService;

    MeterRegistry meterRegistry;

    public BlockerController(BlockerService blockerService,MeterRegistry meterRegistry) {
        this.blockerService = blockerService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/block")
    public ResponseEntity<Boolean> block() {
        var decision = blockerService.block();
        if(decision){
            meterRegistry.counter("blocked_operation").increment();
        }
        return new ResponseEntity<>(decision, HttpStatus.OK);
    }

}
