package com.example;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean @Primary
	public Greeting defaultGreeting() {
		return new Greeting("Default greeting");
	}

	@Bean
	public Greeting alternativeGreeting() {
		return new Greeting("Alternative greeting");
	}
}

@Slf4j
@Component
class BeanLoggingPostProcessor implements BeanPostProcessor {

	private final AtomicInteger counter = new AtomicInteger(0);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		log.info("Created bean no: {} named: {}", counter.incrementAndGet(), beanName);
		return bean;
	}
}

@RestController
class Hello {

	private Greeting greeting;

	public Hello(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello")
	Greeting say() {
		return greeting;
	}
}

@RestController
class Hello2 {

	private Greeting greeting;

	public Hello2(@Qualifier("alternativeGreeting") Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello2")
	Greeting say() {
		return greeting;
	}
}

//@Component
//@Scope("prototype")
@Slf4j
@AllArgsConstructor
@Data
class Greeting {

	private String message;

	Greeting() {
		this.message = "Injected hello!";
	}

	@PostConstruct
	public void init() {
		log.info("New message created: {}", message);
	}
}
