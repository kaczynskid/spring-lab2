package com.example;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mockito;

public class ReservationsServiceImplTest {

	ReservationsRepository repository = Mockito.mock(ReservationsRepository.class);
	ReservationsServiceImpl reservations = new ReservationsServiceImpl(repository);

	@Test
	public void should_not_allow_to_change_name_to_existing_one() throws Exception {
		// given
		when(repository.findOne(5))
			.thenReturn(new Reservation(5, "Marek", "Java"));
		when(repository.findByName("Jan"))
			.thenReturn(new Reservation(6, "Jan", "PLSQL"));

		// when
		Throwable thrown = catchThrowable(() ->
			reservations.update(new Reservation(5, "Jan", "Java")));

		// then
		assertThat(thrown).isInstanceOf(NameNotUnique.class);
		assertThat(thrown.getMessage()).isEqualTo("Reservation for 'Jan' already exists!");
	}
}
