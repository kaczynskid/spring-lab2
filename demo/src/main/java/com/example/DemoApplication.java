package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public Greeting defaultGreeting() {
		return new Greeting("Default greeting");
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

	public Hello2(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello2")
	Greeting say() {
		return greeting;
	}
}

class Greeting {

	private String message;

	Greeting() {
		this.message = "Injected hello!";
	}

	Greeting(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
