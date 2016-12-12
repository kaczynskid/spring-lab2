package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

	@GetMapping("/hello")
	Greeting say() {
		return new Greeting("Hello world changed!");
	}
}

class Greeting {

	private String message;

	Greeting(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
