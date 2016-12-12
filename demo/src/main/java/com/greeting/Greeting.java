package com.greeting;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Data
public class Greeting {

	private String message;

	Greeting() {
		this.message = "Injected hello!";
	}

	@PostConstruct
	public void init() {
		log.info("New message created: {}", message);
	}
}
