package io.openqueue.MVCPerformanceTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@SpringBootApplication
@RestController
public class MvcPerformanceTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcPerformanceTestApplication.class, args);
	}

	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;

	@GetMapping(value = "/hello")
	public String hello() {
		return "Hello!";
	}

	@GetMapping(value = "/sleep/{time}")
	public String sleep(@PathVariable int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Sleep " + time + "ms, Current Time:" + System.currentTimeMillis();
	}

	@GetMapping(value = "/io")
	public String redis() {
		redisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest");
		return "Ok";
	}

	@GetMapping(value = "/io/{times}")
	public String multiIO(@PathVariable int times) {
		assert times > 0;
		for (int i = 0; i < times; i++) {
			redisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest");
		}
		return "Ok";
	}
}
