package com.example;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

@Component
public class ReservationsRepositoryImpl implements LegacyReservationsRepository {

	@PersistenceContext EntityManager jpa;

	@Override
	public Reservation findByName(String name) {
		return jpa.createQuery("from Reservation where name = :name", Reservation.class)
				.setParameter("name", name)
				.getResultList().stream().findFirst().orElse(null);
	}
}
