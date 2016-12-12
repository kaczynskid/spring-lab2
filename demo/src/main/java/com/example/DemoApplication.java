package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
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

@Component
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
