package io.openqueue.WebfluxPerformanceTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@SpringBootApplication
@RestController
public class WebFluxPerformanceTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxPerformanceTestApplication.class, args);
    }

    @Autowired
    private ReactiveRedisTemplate<String, Serializable> reactiveRedisTemplate;

    @GetMapping(value = "/hello")
    public Mono<String> hello() {
        return Mono.just("Hello!");
    }

    @GetMapping("/sleep/{duration}")
    public Mono<String> sleep(@PathVariable int duration) {
        return Mono.delay(Duration.ofMillis(duration))
                .thenReturn("Sleep " + duration + "ms, Current Time:" + System.currentTimeMillis());
    }

    @GetMapping(value = "/io")
    public Mono<String> redis() {
        return reactiveRedisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest")
                .thenReturn("Ok");
    }

    @GetMapping(value = "/io/{times}")
    public Mono<String> multiIO(@PathVariable int times, @RequestParam Integer delay) {
        String value = RandomCodeGenerator.get();
        AtomicInteger index = new AtomicInteger(0);

        Function<Boolean, Mono<Boolean>> redisOperation =
                success -> reactiveRedisTemplate.opsForValue().set(value + ":" + index.incrementAndGet(), value);

        return Mono.just(Boolean.TRUE)
                .flatMap(redisOperation)
                .repeat(times - 1)
                .then(Mono.just("OK"));
    }

    @GetMapping(value = "/delayio/{times}")
    public Mono<String> delayIO(@PathVariable int times, @RequestParam int delay) {
        assert times > 0;
        assert delay >= 0;
        String value = RandomCodeGenerator.get();
        AtomicInteger index = new AtomicInteger(0);

        Function<Boolean, Mono<Boolean>> redisOperation =
                success -> Mono.delay(Duration.ofMillis(delay)).then(reactiveRedisTemplate.opsForValue().set(value + ":" + index.incrementAndGet(), value));

        return Mono.just(Boolean.TRUE)
                .flatMap(redisOperation)
                .repeat(times - 1)
                .then(Mono.just("OK"));
    }
}
