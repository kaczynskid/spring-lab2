package com.example;

import static com.example.QReservation.reservation;

import java.util.List;

import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
public interface ReservationsRepository extends
		JpaRepository<Reservation, Integer>,
		QueryDslPredicateExecutor<Reservation>,
		LegacyReservationsRepository{

	interface Spec {

		static Predicate withLang(String lang) {
			return lang == null ? null : reservation.lang.eq(lang);
		}

		static Predicate withName(String name) {
			return name == null ? null : reservation.name.eq(name);
		}
	}

	@Query("from Reservation where lang = :lang")
	List<Reservation> findByLang(@Param("lang") String lang);

	@Override
	List<Reservation> findAll(Predicate predicate);

	@Override
	@RestResource(exported = false)
	void deleteInBatch(Iterable<Reservation> entities);
}
