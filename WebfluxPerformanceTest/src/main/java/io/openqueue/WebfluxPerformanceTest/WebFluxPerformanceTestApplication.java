package io.openqueue.WebfluxPerformanceTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
		return Mono.delay(Duration.ofMillis(duration)).thenReturn("Sleep " + duration + "ms, Current Time:" + System.currentTimeMillis());
	}

	@GetMapping(value = "/io")
	public Mono<String> redis() {
		long startTime = System.currentTimeMillis();
		return reactiveRedisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest")
				.flatMap(success -> {
					long endTime = System.currentTimeMillis();
					return Mono.just("Redis 请求耗时:" + (endTime - startTime) + "ms");
				});
	}

	@GetMapping(value = "/io/{times}")
	public Mono<String> multiIO(@PathVariable int times) {
		assert times > 0;
		long startTime = System.currentTimeMillis();
		return reactiveRedisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest")
				.repeat(times - 1)
				.last()
				.flatMap(success -> {
					long endTime = System.currentTimeMillis();
					return Mono.just("Redis 请求耗时:" + (endTime - startTime) + "ms");
				});
	}
}
