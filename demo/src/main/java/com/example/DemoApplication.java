package com.example;

import java.util.concurrent.atomic.AtomicInteger;

import com.hello.Hello;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackageClasses = {
	DemoApplication.class, Hello.class
})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoApplication.class);
		app.run(args);
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
