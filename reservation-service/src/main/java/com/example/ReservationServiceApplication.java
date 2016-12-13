package com.example;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

	private final ReservationsService reservations;

	public ReservationController(ReservationsService reservations) {
		this.reservations = reservations;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public List<Reservation> findAll() {
		return reservations.findAll();
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findOne(@PathVariable("id") int id) {
		Optional<Reservation> reservation = reservations.maybeFindById(id);
		if (reservation.isPresent()) {
			return ResponseEntity.ok(reservation.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(CREATED)
	public void create(@RequestBody Reservation reservation) {
		reservations.create(reservation);
	}

	@PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody Reservation reservation) {
		reservation.setId(id);
		reservations.update(reservation);
		return ResponseEntity.accepted().build();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(NO_CONTENT)
	public void delete(@PathVariable("id") int id) {
		reservations.delete(id);
	}

	@ExceptionHandler(NotFound.class)
	public void handleNotFound(NotFound ex, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
	}

	@ExceptionHandler(NameNotUnique.class)
	public void handleNameNotUnique(NameNotUnique ex, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.CONFLICT.value(), ex.getMessage());
	}
}

@Slf4j
@Configuration
class ReservationsServiceConfig {

	@Bean
	public ReservationsService reservationsService() {
		ReservationsServiceImpl service = new ReservationsServiceImpl();

		InvocationHandler loggingHandler = (Object proxy, Method method, Object[] args) -> {
			log.info("Called method: {}", method.getName());
			Object result = method.invoke(service, args);
			log.info("Method: {} returned: {}", method.getName(), result);
			return result;
		};

		ReservationsService wrapped = (ReservationsService) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[] { ReservationsService.class },
				loggingHandler);

		return wrapped;
	}

}

interface ReservationsService {

	List<Reservation> findAll();

	Optional<Reservation> maybeFindById(int id);

	void create(Reservation reservation);

	void update(Reservation reservation);

	void delete(int id);
}

class ReservationsServiceImpl implements ReservationsService {

	private final AtomicInteger seq = new AtomicInteger(0);

	List<Reservation> reservations = Stream.of(
			"Wojtek:Java", "Tomasz:Java", "Piotrek:OracleForms",
			"Robert:PLSQL", "Wiktor:PLSQL", "Grzegorz:Delphi", "Jacek:Delphi",
			"Tomek:PLSQL", "Szymek:PLSQL", "Jacek2:PLSQL", "Grzesiek:PLSQL")
			.map(entry -> entry.split(":"))
			.map(entry -> new Reservation(seq.incrementAndGet(), entry[0], entry[1]))
			.collect(Collectors.toList());

	public List<Reservation> findAll() {
		return reservations;
	}

	public Optional<Reservation> maybeFindById(int id) {
		return reservations.stream()
				.filter(r -> r.getId() == id)
				.findFirst();
	}

	private Optional<Reservation> maybeFindByName(String name) {
		return reservations.stream()
				.filter(r -> r.getName().equals(name))
				.findFirst();
	}

	public void create(Reservation reservation) {
		if (maybeFindByName(reservation.getName())
				.isPresent()) {
			throw new NameNotUnique(reservation.getName());
		}
		reservations.add(new Reservation(
				seq.incrementAndGet(),
				reservation.getName(),
				reservation.getLang()
		));
	}

	public void update(Reservation reservation) {
		Optional<Reservation> existing = maybeFindById(reservation.getId());
		if (!existing.isPresent()) {
			throw new NotFound(reservation.getId());
		}
		if (maybeFindByName(reservation.getName())
				.filter(r -> r.getId() != reservation.getId())
				.isPresent()) {
			throw new NameNotUnique(reservation.getName());
		}
		existing
				.map(r -> {
					r.setName(reservation.getName());
					r.setLang(reservation.getLang());
					return r;
				});
	}

	public void delete(int id) {
		Optional<Reservation> reservation = maybeFindById(id);
		if (reservation.isPresent()) {
			reservations.remove(reservation.get());
		}
	}
}

class NotFound extends RuntimeException {

	NotFound(int id) {
		super("No reservation found for id '" + id + "'!");
	}
}

class NameNotUnique extends RuntimeException {

	NameNotUnique(String name) {
		super("Reservation for '" + name + "' already exists!");
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

	private int id;

	private String name;

	private String lang;
}
