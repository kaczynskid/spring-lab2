package com.example;

import static javax.persistence.GenerationType.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.transaction.annotation.Propagation.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public List<Reservation> findAll(@RequestParam(name = "lang", required = false) String lang) {
		if (lang != null) {
			return reservations.findByLang(lang);
		}
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

	@ExceptionHandler(InvocationTargetException.class)
	public void handleInvocationException(InvocationTargetException ex, HttpServletResponse response) throws IOException {
		if (ex.getTargetException() instanceof NameNotUnique) {
			handleNameNotUnique((NameNotUnique) ex.getTargetException(), response);
		} else if (ex.getTargetException() instanceof NotFound) {
			handleNotFound((NotFound) ex.getTargetException(), response);
		} else {
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
		}
	}
}

@Slf4j
@Configuration
@EnableAspectJAutoProxy
@EnableJpaRepositories
class ReservationsServiceConfig {

	@Autowired
	ReservationsRepository reservations;

	@Bean
	public ReservationsService reservationsService() {
		ReservationsServiceImpl service = new ReservationsServiceImpl(reservations);

		InvocationHandler loggingHandler = (Object proxy, Method method, Object[] args) -> {
			log.info("BEFORE method: {}", method.getName());
			Object result = method.invoke(service, args);
			log.info("AFTER method: {}. RETURNED: {}", method.getName(), result);
			return result;
		};

		ReservationsService wrapped = (ReservationsService) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[] { ReservationsService.class },
				loggingHandler);

		return wrapped;
	}
}

@Slf4j
@Aspect
@Component
class MonitorAspect {

	@Pointcut("execution(* com.example.ReservationsService.*(..))")
	private void anyServiceOperation() {}

	@Around("anyServiceOperation()")
	public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		String name = joinPoint.getSignature().getName();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start(name);
		Object result = joinPoint.proceed();
		stopWatch.stop();
		log.info("MONITOR method {} took {}ms", name, stopWatch.getLastTaskInfo().getTimeMillis());
		return result;
	}
}

@Transactional
interface ReservationsService {

	@Transactional(propagation = SUPPORTS, readOnly = true)
	List<Reservation> findAll();

	@Transactional(propagation = SUPPORTS, readOnly = true)
	List<Reservation> findByLang(String lang);

	@Transactional(propagation = SUPPORTS, readOnly = true)
	Optional<Reservation> maybeFindById(int id);

	void create(Reservation reservation);

	void update(Reservation reservation);

	void delete(int id);
}

class ReservationsServiceImpl implements ReservationsService {

	private final ReservationsRepository reservations;

	public ReservationsServiceImpl(ReservationsRepository reservations) {
		this.reservations = reservations;
	}

	@Override
	public List<Reservation> findAll() {
		return reservations.findAll();
	}

	@Override
	public List<Reservation> findByLang(String lang) {
		return reservations.findByLang(lang);
	}

	public Optional<Reservation> maybeFindById(int id) {
		return Optional.ofNullable(reservations.findOne(id));
	}

	private Optional<Reservation> maybeFindByName(String name) {
		return Optional.ofNullable(reservations.findByName(name));
	}

	public void create(Reservation reservation) {
		if (maybeFindByName(reservation.getName())
				.isPresent()) {
			throw new NameNotUnique(reservation.getName());
		}
		reservations.save(reservation);
	}

	public void update(Reservation reservation) {
		Optional<Reservation> existing = maybeFindById(reservation.getId());
		if (!existing.isPresent()) {
			throw new NotFound(reservation.getId());
		}
		if (maybeFindByName(reservation.getName())
				.filter(r -> !r.getId().equals(reservation.getId()))
				.isPresent()) {
			throw new NameNotUnique(reservation.getName());
		}
		existing.map(r -> {
			r.setName(reservation.getName());
			r.setLang(reservation.getLang());
			reservations.save(r);
			return r;
		});

	}

	public void delete(int id) {
		reservations.delete(id);
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

@Component
class ReservationsInitializer implements ApplicationRunner {

	private final ReservationsRepository reservations;

	public ReservationsInitializer(ReservationsRepository reservations) {
		this.reservations = reservations;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		Stream.of(
			"Wojtek:Java", "Tomasz:Java", "Piotrek:OracleForms",
			"Robert:PLSQL", "Wiktor:PLSQL", "Grzegorz:Delphi", "Jacek:Delphi",
			"Tomek:PLSQL", "Szymek:PLSQL", "Jacek2:PLSQL", "Grzesiek:PLSQL")
			.map(entry -> entry.split(":"))
			.map(entry -> new Reservation(entry[0], entry[1]))
			.filter(r -> reservations.findByName(r.getName()) == null)
			.forEach(reservations::save);
	}
}

interface ReservationsRepository extends JpaRepository<Reservation, Integer> {

	Reservation findByName(String name);

	@Query("from Reservation where lang = :lang")
	List<Reservation> findByLang(@Param("lang") String lang);
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
class Reservation {

	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "reservation_seq")
	@SequenceGenerator(name = "reservation_seq", sequenceName = "reservation_seq")
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String lang;

	public Reservation(String name, String lang) {
		this(null, name, lang);
	}
}
