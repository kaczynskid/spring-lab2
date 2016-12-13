package com.example;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}
}

@RestController
@RequestMapping("/reservations")
class ReservationController {

	@GetMapping
	public List<Reservation> findAll() {
		return Stream.of(
				"Wojtek:Java", "Tomasz:Java", "Piotrek:OracleForms",
				"Robert:PLSQL", "Wiktor:PLSQL", "Grzesiek:Delphi", "Jacek:Delphi",
				"Tomek:PLSQL", "Szymek:PLSQL", "Jacek:PLSQL", "Grzesiek:PLSQL")
				.map(entry -> entry.split(":"))
				.map(entry -> new Reservation(entry[0], entry[1]))
				.collect(Collectors.toList());
	}
}

@Value
class Reservation {

	private String name;

	private String lang;
}
