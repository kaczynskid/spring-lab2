package com.example;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationsPersistenceTest {

    @Autowired TestEntityManager entityManager;
    @Autowired ReservationsRepository reservations;

    @Test
    public void should_persist_reservations() throws Exception {
        // when
        Reservation reservation = reservations.save(new Reservation("Test", "Java"));
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(entityManager.find(Reservation.class, reservation.getId()).getName())
            .isEqualTo(reservation.getName());
    }

    @Test
    public void should_find_by_name() throws Exception {
        // given
        Reservation reservation = entityManager.persistAndFlush(new Reservation("John", "Java"));
        entityManager.flush();
        entityManager.clear();

        // when
        Reservation result = reservations.findByName("John");

        // then
        assertThat(result.getId()).isEqualTo(reservation.getId());
        assertThat(result.getName()).isEqualTo(reservation.getName());
    }
}
