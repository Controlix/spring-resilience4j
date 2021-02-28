package be.ict.mb.springresilience4j;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
        log.debug("Received %s", time);

        return time.getTime();
    }

}
