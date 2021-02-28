package be.ict.mb.springresilience4j;

import io.github.resilience4j.decorators.Decorators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

@Slf4j
@RestController
@RequestMapping("/time")
public class TimeController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Supplier<TimeMessage> timeSupplier = () -> restTemplate.getForObject("http://localhost:8080/time", TimeMessage.class);

    @GetMapping("/raw")
    public String justGetTheTime() {
        TimeMessage time = timeSupplier.get();
        log.debug("Received {}", time);

        return time.getTime();
    }

    @GetMapping("/default")
    public String uponFailureUseGMTTime() {
        TimeMessage time = Decorators.ofSupplier(timeSupplier)
                .withFallback(e -> {
                    log.warn("Could not obtian time, fallback to UTC time");
                    return new TimeMessage(LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                })
                .get();
        log.debug("Received {}", time);

        return time.getTime();
    }
}
