package com.hello;

import com.greeting.Greeting;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello2 {

	private Greeting greeting;

	public Hello2(@Qualifier("alternativeGreeting") Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello2")
	Greeting say() {
		return greeting;
	}
}
