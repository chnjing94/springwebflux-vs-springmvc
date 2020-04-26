package io.openqueue.WebfluxPerformanceTest;

import io.lettuce.core.dynamic.annotation.Param;
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
    public Mono<String> multiIO(@PathVariable int times) {
    	assert times > 0;
        return reactiveRedisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest")
                .repeat(times - 1)
                .last()
                .flatMap(success -> Mono.just("Ok"));
    }
}
