package com.example;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

	private final AtomicInteger seq = new AtomicInteger(0);

	List<Reservation> reservations = Stream.of(
			"Wojtek:Java", "Tomasz:Java", "Piotrek:OracleForms",
			"Robert:PLSQL", "Wiktor:PLSQL", "Grzesiek:Delphi", "Jacek:Delphi",
			"Tomek:PLSQL", "Szymek:PLSQL", "Jacek:PLSQL", "Grzesiek:PLSQL")
			.map(entry -> entry.split(":"))
			.map(entry -> new Reservation(seq.incrementAndGet(), entry[0], entry[1]))
			.collect(Collectors.toList());

	@GetMapping
	public List<Reservation> findAll() {
		return reservations;
	}

	public Reservation findOne(int id) {
		return null;
	}

	public void create(Reservation reservation) {

	}

	public void update(int id, Reservation reservation) {

	}

	public void delete(int id) {

	}
}

@Value
class Reservation {

	private int id;

	private String name;

	private String lang;
}
