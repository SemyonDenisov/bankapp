package ru.yandex.account.controller;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.account.service.BlockerService;
import ru.yandex.account.service.LogService;

@RestController
public class BlockerController {

    BlockerService blockerService;

    MeterRegistry meterRegistry;

    Tracer tracer;

    LogService log;



    public BlockerController(BlockerService blockerService,MeterRegistry meterRegistry,Tracer tracer,LogService log) {
        this.blockerService = blockerService;
        this.meterRegistry = meterRegistry;
        this.tracer = tracer;
        this.log = log;
    }

    @GetMapping("/block")
    public ResponseEntity<Boolean> block() {
        if (tracer.currentSpan() != null) {
            ThreadContext.put("traceId", tracer.currentSpan().context().traceId());
            ThreadContext.put("spanId", tracer.currentSpan().context().spanId());
        } else {
            ThreadContext.put("traceId", "none");
            ThreadContext.put("spanId", "none");
        }

        var decision = blockerService.block();
        if(decision){
            log.error("request is blocked");
            meterRegistry.counter("blocked_operation").increment();
        }
        log.info("request was not blocked");
        return new ResponseEntity<>(decision, HttpStatus.OK);
    }

}
