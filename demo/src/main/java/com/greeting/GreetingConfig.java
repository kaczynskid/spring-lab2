package com.greeting;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class GreetingConfig {

	@Autowired
	Environment environment;

	@Bean
	@Primary
	public Greeting defaultGreeting() {
		return new Greeting(environment.getProperty("greeting.default-msg"));
	}

	@Bean
	public Greeting alternativeGreeting() {
		return new Greeting(environment.getProperty("greeting.alternative-msg"));
	}
}

