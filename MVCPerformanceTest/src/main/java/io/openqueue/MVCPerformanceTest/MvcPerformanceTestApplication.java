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
		long startTime = System.currentTimeMillis();
		redisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest");
		long endTime = System.currentTimeMillis();
		return "Redis 请求耗时:" + (endTime - startTime) + "ms";
	}

	@GetMapping(value = "/multi_io_3")
	public String nio3() {
		long startTime = System.currentTimeMillis();
		redisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest");
		redisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest");
		redisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest");
		long endTime = System.currentTimeMillis();
		return "Redis 请求耗时:" + (endTime - startTime) + "ms";
	}

	@GetMapping(value = "/multi_io_10")
	public String nio10() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			redisTemplate.opsForValue().set(RandomCodeGenerator.get(), "iotest");
		}
		long endTime = System.currentTimeMillis();
		return "Redis 请求耗时:" + (endTime - startTime) + "ms";
	}
}
