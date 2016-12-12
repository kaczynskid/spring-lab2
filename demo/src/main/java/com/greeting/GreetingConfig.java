package com.greeting;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(GreetingProps.class)
public class GreetingConfig {

	@Autowired
	GreetingProps props;

	@Bean
	@Primary
	public Greeting defaultGreeting() {
		return new Greeting(props.getDefaultMsg());
	}

	@Bean
	@Profile("alt")
	public Greeting alternativeGreeting() {
		return new Greeting(props.getAlternativeMsg());
	}
}

@Data
@ConfigurationProperties(prefix = "greeting")
class GreetingProps {

	private String defaultMsg;

	private String alternativeMsg;
}

