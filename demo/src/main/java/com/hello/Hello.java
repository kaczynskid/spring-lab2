package com.hello;

import com.greeting.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

	private Greeting greeting;

	public Hello(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello")
	Greeting say() {
		return greeting;
	}
}
