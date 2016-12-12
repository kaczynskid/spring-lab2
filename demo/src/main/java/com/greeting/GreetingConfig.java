package com.greeting;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class GreetingConfig {

	@Bean
	@Primary
	public Greeting defaultGreeting() {
		return new Greeting("Default greeting");
	}

	@Bean
	public Greeting alternativeGreeting() {
		return new Greeting("Alternative greeting");
	}
}

