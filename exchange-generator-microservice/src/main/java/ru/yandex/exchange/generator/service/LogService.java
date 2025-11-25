package ru.yandex.exchange.generator.service;


import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class LogService {

    Tracer tracer;

    public LogService(Tracer tracer) {
        this.tracer = tracer;
    }

    public void putSpecificFields() {
        if (tracer.currentSpan() != null) {
            ThreadContext.put("traceId", tracer.currentSpan().context().traceId());
            ThreadContext.put("spanId", tracer.currentSpan().context().spanId());
        } else {
            ThreadContext.put("traceId", "none");
            ThreadContext.put("spanId", "none");
        }
    }

    public void info(String message) {
        putSpecificFields();
        log.info(message);
    }

    public void warn(String message) {
        putSpecificFields();
        log.warn(message);
    }

    public void error(String message) {
        putSpecificFields();
        log.error(message);
    }
}
