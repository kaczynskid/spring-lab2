package com.example;

import static com.example.QReservation.reservation;
import static java.lang.String.format;

import java.util.List;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

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

@Component
class ReservationResourceProcessor implements ResourceProcessor<Resource<Reservation>> {

	@Override
	public Resource<Reservation> process(Resource<Reservation> resource) {
		Reservation reservation = resource.getContent();
		String url = format("https://www.google.pl/search?tbm=isch&q=%s",
				reservation.getName());
		resource.add(new Link(url, "photo"));
		return resource;
	}
}
